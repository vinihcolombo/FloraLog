const usuarioLogado = JSON.parse(localStorage.getItem('usuarioLogado'));
const USUARIO_ID_LOGADO = usuarioLogado ? (usuarioLogado.id || usuarioLogado.usuarioId) : null;

// Nova URL base alinhada ao padrão aninhado do utilizador
const API_URL = `http://localhost:8080/api/usuarios/${USUARIO_ID_LOGADO}/biblioteca`;

let listaPlantasGlobal = [];

if (!USUARIO_ID_LOGADO) {
    window.location.href = 'index.html';
}

document.addEventListener('DOMContentLoaded', () => {
    carregarPlantas();

    const formPlanta = document.getElementById('formPlanta');
    if (formPlanta) {
        formPlanta.addEventListener('submit', (e) => {
            e.preventDefault();
            salvarPlanta();
        });
    }

    const btnImportar = document.getElementById('btnConfirmarImportacao') || document.getElementById('btnImportar');
    if (btnImportar) {
        btnImportar.addEventListener('click', importarPlantaPorCodigoUnico);
    }
});

async function carregarPlantas() {
    try {
        const response = await fetch(API_URL);
        if (!response.ok) throw new Error('Erro ao buscar plantas do servidor.');

        listaPlantasGlobal = await response.json();
        renderizarGrid(listaPlantasGlobal);
    } catch (error) {
        console.error('Erro ao carregar a biblioteca:', error);
        mostrarErroGrid('Erro ao conectar com o servidor. Tente novamente mais tarde.');
    }
}

function renderizarGrid(plantas) {
    const grid = document.getElementById('gridPlantas');
    if (!grid) return;

    if (!plantas || plantas.length === 0) {
        grid.innerHTML = `
            <div class="col-12 text-center py-5">
                <div class="text-flora-muted mb-2"><i class="bi bi-flower3 fs-1 opacity-50"></i></div>
                <p class="text-flora-muted fw-medium">Nenhuma espécie cadastrada na biblioteca.</p>
            </div>
        `;
        return;
    }

    grid.innerHTML = plantas.map(p => `
        <div class="col">
            <div class="card bg-flora-card border border-flora-green h-100 shadow-sm">
                <div class="card-body d-flex flex-column p-4">
                    <div class="d-flex gap-3 mb-3">
                        <div class="flex-shrink-0">
                            <div class="rounded-4 bg-flora-dark border border-flora-green overflow-hidden d-flex align-items-center justify-content-center position-relative"
                                 style="width: 90px; height: 90px; cursor: pointer;"
                                 onclick="abrirSeletorFoto('${p.id}')" title="Clique para alterar a foto">
                                ${p.fotoUrl ?
                                    `<img src="${p.fotoUrl}" alt="${p.nome}" class="w-100 h-100 object-fit-cover">` :
                                    `<i class="bi bi-camera text-flora-muted fs-4"></i>`
                                }
                            </div>
                        </div>
                        <div class="flex-grow-1 d-flex flex-column">
                            <div class="d-flex justify-content-between align-items-start mb-1">
                                <h5 class="card-title fw-bold text-flora-green mb-0">${p.nome || p.nomePopular || ''}</h5>
                                <span class="badge px-2 py-1 fw-semibold ${obterClasseDificuldade(p.nivelDificuldade)}">${p.nivelDificuldade || 'Fácil'}</span>
                            </div>
                            <h6 class="card-subtitle text-flora-muted fst-italic small mb-2">${p.nomeCientifico || 'Espécie não informada'}</h6>
                            <p class="card-text text-flora-main small mb-0">${p.descricao || 'Sem orientações de cuidado informadas.'}</p>
                        </div>
                    </div>
                    <div class="mt-auto">
                        <div class="bg-flora-dark rounded p-3 mb-3 border border-flora-green small text-flora-main">
                            <div class="mb-1"><i class="bi bi-sun me-2 text-warning"></i> <strong>Luz:</strong> ${p.iluminacao || 'Não especificado'}</div>
                            <div class="mb-1"><i class="bi bi-droplet me-2 text-info"></i> <strong>Rega:</strong> ${p.rega || 'Não especificado'}</div>
                            <div class="mb-2"><i class="bi bi-thermometer-half me-2 text-danger"></i> <strong>Temp:</strong> ${p.temperatura || 'Não especificado'}</div>
                            <div class="text-secondary small border-top border-secondary pt-1 mt-1" style="cursor: pointer;" onclick="copiarCodigo('${p.codigoPartilha || ''}', this)" title="Clique para copiar o código">
                                <i class="bi bi-share me-1"></i> <strong>Código:</strong> <code class="codigo-texto">${p.codigoPartilha || 'N/A'}</code>
                                <span class="ms-1 text-success small feedback-copiado" style="display: none;"><i class="bi bi-check-lg"></i> Copiado!</span>
                            </div>
                        </div>
                        <div class="d-flex justify-content-between align-items-center pt-3 border-top border-flora-green">
                            <span class="badge bg-secondary text-light text-uppercase px-2 py-1">${p.categoria || 'Geral'}</span>
                            <div class="d-flex gap-2">
                                <button class="btn btn-sm btn-outline-secondary border-flora-green text-flora-main px-2" onclick="editarPlanta('${p.id}')" title="Editar"><i class="bi bi-pencil"></i></button>
                                <button class="btn btn-sm btn-outline-danger px-2" onclick="excluirPlanta('${p.id}')" title="Excluir"><i class="bi bi-trash"></i></button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `).join('');
}

async function importarPlantaPorCodigoUnico() {
    const input = document.getElementById('inputCodigoPartilha');
    const codigo = input ? input.value.trim() : '';

    if (!codigo) {
        alert('Por favor, insira um código de partilha válido.');
        return;
    }

    try {
        const response = await fetch(`${API_URL}/importar/${codigo}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        });

        if (response.ok) {
            const modalEl = document.getElementById('modalAdicionarCodigo');
            const modal = bootstrap.Modal.getInstance(modalEl);
            if (modal) modal.hide();

            input.value = '';
            carregarPlantas();
            alert('Espécie importada com sucesso para a sua biblioteca!');
        } else {
            const erroMsg = await response.text();
            alert('Erro ao importar: ' + (erroMsg || 'Código inválido.'));
        }
    } catch (error) {
        console.error('Erro na importação:', error);
        alert('Erro de conexão ao tentar importar a espécie.');
    }
}

async function salvarPlanta() {
    const id = document.getElementById('plantaId').value;

    if (id) {
        const confirmado = await showConfirmModal('Tem certeza de que deseja salvar as alterações desta espécie?');
        if (!confirmado) return;
    }

    const dadosPlanta = {
        id: id || undefined,
        nome: document.getElementById('nomePopular').value,
        nomeCientifico: document.getElementById('nomeCientifico').value,
        categoria: document.getElementById('categoria').value,
        nivelDificuldade: document.getElementById('nivelDificuldade').value,
        iluminacao: document.getElementById('iluminacao').value,
        rega: document.getElementById('rega').value,
        temperatura: document.getElementById('temperatura').value,
        descricao: document.getElementById('descricao').value
    };

    try {
        const url = id ? `${API_URL}/${id}` : API_URL;
        const metodo = id ? 'PUT' : 'POST';

        const response = await fetch(url, {
            method: metodo,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dadosPlanta)
        });

        if (!response.ok) throw new Error('Erro ao salvar a planta no servidor.');

        await carregarPlantas();

        const modalEl = document.getElementById('modalPlanta');
        const modal = bootstrap.Modal.getInstance(modalEl);
        if (modal) modal.hide();

        limparFormulario();
    } catch (error) {
        console.error('Erro ao salvar planta:', error);
        alert('Não foi possível salvar a planta.');
    }
}

function editarPlanta(id) {
    const planta = listaPlantasGlobal.find(p => p.id === id);
    if (!planta) return;

    document.getElementById('plantaId').value = planta.id;
    document.getElementById('nomePopular').value = planta.nome || planta.nomePopular || '';
    document.getElementById('nomeCientifico').value = planta.nomeCientifico || '';
    document.getElementById('categoria').value = planta.categoria || '';
    document.getElementById('nivelDificuldade').value = planta.nivelDificuldade || 'Fácil';
    document.getElementById('iluminacao').value = planta.iluminacao || '';
    document.getElementById('rega').value = planta.rega || '';
    document.getElementById('temperatura').value = planta.temperatura || '';
    document.getElementById('descricao').value = planta.descricao || '';

    document.getElementById('modalTitulo').innerHTML = '<i class="bi bi-pencil-square me-2"></i>Editar Espécie';

    const modal = new bootstrap.Modal(document.getElementById('modalPlanta'));
    modal.show();
}

function limparFormulario() {
    const form = document.getElementById('formPlanta');
    if (form) form.reset();
    document.getElementById('plantaId').value = '';
    document.getElementById('modalTitulo').innerHTML = '<i class="bi bi-flower2 me-2"></i>Cadastrar Espécie';
}

async function excluirPlanta(id) {
    const confirmado = await showConfirmModal('Tem certeza de que deseja excluir esta espécie da biblioteca?');
    if (!confirmado) return;

    try {
        const response = await fetch(`${API_URL}/${id}`, { method: 'DELETE' });
        if (!response.ok) throw new Error('Erro ao excluir planta no servidor.');

        await carregarPlantas();
    } catch (error) {
        console.error('Erro ao excluir:', error);
        alert('Não foi possível excluir a planta.');
    }
}

function showConfirmModal(mensagem) {
    return new Promise((resolve) => {
        const existingModal = document.getElementById('customConfirmModal');
        if (existingModal) existingModal.remove();

        const modalHtml = `
            <div class="modal fade" id="customConfirmModal" tabindex="-1" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content bg-flora-card border-flora-green text-body shadow-lg">
                        <div class="modal-header border-bottom border-flora-green">
                            <h5 class="modal-title text-flora-green fw-bold">
                                <i class="bi bi-exclamation-triangle-fill me-2"></i> Confirmação
                            </h5>
                            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Fechar"></button>
                        </div>
                        <div class="modal-body py-4">
                            <p class="mb-0 fs-6">${mensagem}</p>
                        </div>
                        <div class="modal-footer border-top border-flora-green">
                            <button type="button" class="btn btn-outline-secondary btn-sm px-3" data-bs-dismiss="modal" id="cancelBtn">Cancelar</button>
                            <button type="button" class="btn btn-flora-green btn-sm px-3" id="confirmBtn">Confirmar</button>
                        </div>
                    </div>
                </div>
            </div>
        `;

        document.body.insertAdjacentHTML('beforeend', modalHtml);

        const modalElement = document.getElementById('customConfirmModal');
        const bsModal = new bootstrap.Modal(modalElement);
        bsModal.show();

        const confirmBtn = document.getElementById('confirmBtn');
        let result = false;

        confirmBtn.addEventListener('click', () => {
            result = true;
            bsModal.hide();
        });

        modalElement.addEventListener('hidden.bs.modal', () => {
            modalElement.remove();
            resolve(result);
        });
    });
}

function abrirSeletorFoto(id) {
    const planta = listaPlantasGlobal.find(p => p.id === id);
    if (!planta) return;

    const novaUrl = prompt(`Informe o link (URL) da imagem para a espécie "${planta.nome || planta.nomePopular}":`, planta.fotoUrl || '');

    if (novaUrl !== null) {
        atualizarFotoPlanta(id, novaUrl.trim());
    }
}

async function atualizarFotoPlanta(id, fotoUrl) {
    try {
        const planta = listaPlantasGlobal.find(p => p.id === id);
        if (!planta) return;

        const dadosAtualizados = { ...planta, fotoUrl };

        const response = await fetch(`${API_URL}/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dadosAtualizados)
        });

        if (!response.ok) throw new Error('Erro ao atualizar a foto da planta.');

        await carregarPlantas();
    } catch (error) {
        console.error('Erro ao salvar foto:', error);
        alert('Não foi possível atualizar a foto.');
    }
}

function filtrarPlantas() {
    const termo = document.getElementById('inputBusca').value.toLowerCase();
    const selectEl = document.getElementById('selectFiltroDificuldade');
    const dificuldade = selectEl ? selectEl.value : '';

    const filtradas = listaPlantasGlobal.filter(p => {
        const nomePlanta = p.nome || p.nomePopular || '';
        const textoMatch = nomePlanta.toLowerCase().includes(termo) ||
                           (p.nomeCientifico && p.nomeCientifico.toLowerCase().includes(termo)) ||
                           (p.categoria && p.categoria.toLowerCase().includes(termo));

        const dificuldadeMatch = !dificuldade || p.nivelDificuldade === dificuldade;

        return textoMatch && dificuldadeMatch;
    });

    renderizarGrid(filtradas);
}

function mostrarErroGrid(mensagem) {
    const grid = document.getElementById('gridPlantas');
    if (grid) {
        grid.innerHTML = `
            <div class="col-12 text-center py-5">
                <div class="text-danger mb-2"><i class="bi bi-exclamation-triangle fs-1"></i></div>
                <p class="text-flora-muted fw-medium">${mensagem}</p>
            </div>
        `;
    }
}

async function copiarCodigo(codigo, elemento) {
    if (!codigo || codigo === 'N/A') return;

    try {
        await navigator.clipboard.writeText(codigo);

        const feedback = elemento.querySelector('.feedback-copiado');
        const codigoTag = elemento.querySelector('.codigo-texto');

        if (feedback && codigoTag) {
            codigoTag.style.display = 'none';
            feedback.style.display = 'inline';

            setTimeout(() => {
                feedback.style.display = 'none';
                codigoTag.style.display = 'inline';
            }, 2000);
        }
    } catch (error) {
        console.error('Erro ao copiar código:', error);
        alert('Não foi possível copiar o código.');
    }
}

function obterClasseDificuldade(nivel) {
    switch (nivel) {
        case 'Fácil':
            return 'bg-success-subtle text-success border border-success-subtle';
        case 'Médio':
            return 'bg-warning-subtle text-warning border border-warning-subtle';
        case 'Difícil':
            return 'bg-danger-subtle text-danger border border-danger-subtle';
        default:
            return 'bg-secondary-subtle text-secondary border border-secondary-subtle';
    }
}