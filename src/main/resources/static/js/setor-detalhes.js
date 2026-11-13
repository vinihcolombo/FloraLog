const urlParams = new URLSearchParams(window.location.search);
const setorId = urlParams.get('id');

const usuarioLogadoId = (() => {
    const usuarioStr = localStorage.getItem('usuarioLogado') || localStorage.getItem('usuarioId');
    if (!usuarioStr) return null;
    try {
        const parsed = JSON.parse(usuarioStr);
        return parsed.id || parsed._id || parsed.usuarioId;
    } catch(e) {
        return usuarioStr;
    }
})();

// Nova URL base para os setores do utilizador
const API_BASE_URL = `http://localhost:8080/api/usuarios/${usuarioLogadoId}/setores`;

let setorAtualGlobal = null;
let plantasBibliotecaCache = [];

document.addEventListener("DOMContentLoaded", () => {
    if (!setorId || !usuarioLogadoId) {
        alert('Parâmetros inválidos ou sessão expirada.');
        window.location.href = 'horta-casa.html';
        return;
    }
    carregarDetalhesESetorPlantas();
    carregarBibliotecaParaModal();

    const selectFreq = document.getElementById('frequenciaTarefa');
    if (selectFreq) {
        selectFreq.addEventListener('change', (e) => {
            const divPers = document.getElementById('divPersonalizado');
            if (e.target.value === 'Personalizado') {
                divPers.style.display = 'block';
            } else {
                divPers.style.display = 'none';
            }
        });
    }
});

// Como os setores estão embutidos no utilizador, buscamos a lista de setores e filtramos o atual
async function carregarDetalhesESetorPlantas() {
    try {
        const response = await fetch(API_BASE_URL);
        if (response.ok) {
            const setores = await response.json();
            setorAtualGlobal = setores.find(s => s.id === setorId);

            if (!setorAtualGlobal) {
                alert('Setor não encontrado.');
                window.location.href = 'horta-casa.html';
                return;
            }

            // Renderizar detalhes do setor
            document.getElementById('nomeSetorTitulo').innerHTML = `<i class="bi bi-flower2 text-flora-green me-2"></i> ${setorAtualGlobal.nome}`;
            document.getElementById('descricaoSetorTexto').textContent = setorAtualGlobal.descricao || 'Sem descrição informada.';

            // Renderizar plantas do setor
            renderizarPlantasDoSetor(setorAtualGlobal.plantas || []);
        }
    } catch (error) {
        console.error("Erro ao carregar detalhes do setor:", error);
    }
}

function renderizarPlantasDoSetor(plantasDoSetor) {
    const grid = document.getElementById('gridPlantasSetor');
    grid.innerHTML = '';

    document.getElementById('totalPlantasSetor').textContent = plantasDoSetor.length;

    if (!plantasDoSetor || plantasDoSetor.length === 0) {
        grid.innerHTML = `<div class="text-center py-4 text-flora-muted card bg-flora-dark border border-flora-green p-4 rounded-3">Nenhuma planta neste setor ainda. Clique em "Adicionar da Biblioteca".</div>`;
        document.getElementById('totalRegasPendentes').textContent = '0';
        return;
    }

    let totalPendentesGeral = 0;

    plantasDoSetor.forEach(vinculo => {
        // Calcular tarefas pendentes desta planta
        const tarefas = vinculo.tarefas || [];
        const pendentes = tarefas.filter(t => !t.concluida);
        totalPendentesGeral += pendentes.length;

        let visualMedia = '';
        if (vinculo.fotoUrl && vinculo.fotoUrl.trim() !== '') {
            visualMedia = `<img src="${vinculo.fotoUrl}" alt="${vinculo.nome}" class="rounded-3 border border-flora-green" width="70" height="70" style="object-fit: cover;">`;
        } else {
            visualMedia = `
                <div class="rounded-3 bg-flora-black border border-flora-green d-flex align-items-center justify-content-center" style="width: 70px; height: 70px; min-width: 70px;">
                    <i class="bi bi-flower1 fs-3 text-flora-green"></i>
                </div>`;
        }

        grid.innerHTML += `
            <div class="card bg-flora-dark border border-flora-green shadow-sm rounded-3 p-3 mb-3">
                <div class="d-flex align-items-start justify-content-between flex-wrap gap-3">
                    <div class="d-flex align-items-start gap-3">
                        ${visualMedia}
                        <div>
                            <h4 class="fw-bold text-flora-main m-0">${vinculo.nome || 'Planta'}</h4>
                            <span class="text-flora-muted small fst-italic d-block mb-2">${vinculo.nomeCientifico || 'Espécie não informada'}</span>

                            <div class="d-flex flex-wrap gap-3 text-small text-flora-muted">
                                <span><i class="bi bi-sun text-warning me-1"></i> Luz: ${vinculo.iluminacao || 'N/A'}</span>
                                <span><i class="bi bi-droplet text-info me-1"></i> Rega: ${vinculo.rega || 'N/A'}</span>
                                <span><i class="bi bi-thermometer-half text-danger me-1"></i> Temp: ${vinculo.temperatura || 'N/A'}</span>
                            </div>
                        </div>
                    </div>
                    <div class="d-flex align-items-center gap-2">
                        <button class="btn btn-outline-danger btn-sm" onclick="removerVinculo('${vinculo.id}')" title="Remover do Setor">
                            <i class="bi bi-trash"></i>
                        </button>
                        <button class="btn btn-outline-secondary border-flora-green text-flora-main btn-sm d-flex align-items-center gap-1" type="button" data-bs-toggle="collapse" data-bs-target="#collapseTarefa-${vinculo.id}" aria-expanded="false" onclick="renderizarTarefas('${vinculo.id}')">
                            <i class="bi bi-chevron-down"></i> Tarefas (${tarefas.length})
                        </button>
                    </div>
                </div>

                <!-- Gaveta de Tarefas / Atividades -->
                <div class="collapse mt-3 pt-3 border-top border-flora-green border-opacity-25" id="collapseTarefa-${vinculo.id}">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h6 class="fw-bold text-flora-green m-0"><i class="bi bi-list-check me-1"></i> Cronograma e Tarefas</h6>
                        <button class="btn btn-flora-green btn-sm fw-bold" onclick="abrirModalNovaTarefa('${vinculo.id}')">
                            <i class="bi bi-plus-lg me-1"></i> Adicionar Tarefa
                        </button>
                    </div>

                    <!-- Seção de Afazeres Pendentes -->
                    <div class="mb-3">
                        <span class="text-warning small fw-bold text-uppercase d-block mb-2"><i class="bi bi-clock-history me-1"></i> Afazeres Pendentes</span>
                        <div id="listaPendentes-${vinculo.id}" class="d-flex flex-column gap-2">
                            <!-- Preenchido dinamicamente -->
                        </div>
                    </div>

                    <!-- Seção de Tarefas Concluídas -->
                    <div>
                        <span class="text-flora-green small fw-bold text-uppercase d-block mb-2"><i class="bi bi-check2-all me-1"></i> Concluídas</span>
                        <div id="listaConcluidas-${vinculo.id}" class="d-flex flex-column gap-2">
                            <!-- Preenchido dinamicamente -->
                        </div>
                    </div>
                </div>
            </div>
        `;

        // Renderiza as tarefas imediatamente se a planta tiver dados em cache
        setTimeout(() => renderizarTarefas(vinculo.id), 50);
    });

    document.getElementById('totalRegasPendentes').textContent = totalPendentesGeral;
}

function renderizarTarefas(setorPlantaId) {
    if (!setorAtualGlobal || !setorAtualGlobal.plantas) return;

    const vinculo = setorAtualGlobal.plantas.find(p => p.id === setorPlantaId);
    if (!vinculo) return;

    const containerPendentes = document.getElementById(`listaPendentes-${setorPlantaId}`);
    const containerConcluidas = document.getElementById(`listaConcluidas-${setorPlantaId}`);
    if (!containerPendentes || !containerConcluidas) return;

    const tarefas = vinculo.tarefas || [];
    const pendentes = tarefas.filter(t => !t.concluida);
    const concluidas = tarefas.filter(t => t.concluida);

    containerPendentes.innerHTML = '';
    containerConcluidas.innerHTML = '';

    if (pendentes.length === 0) {
        containerPendentes.innerHTML = `<div class="text-muted small py-1 fst-italic">Nenhum afazer pendente no momento. Tudo em dia! 🌱</div>`;
    } else {
        pendentes.forEach(t => {
            let iconeTipo = 'bi-list-task text-flora-muted';
            const tipoFormatado = (t.tipo || '').toLowerCase();

            if (tipoFormatado.includes('rega')) iconeTipo = 'bi-droplet-fill text-info';
            else if (tipoFormatado.includes('adub') || tipoFormatado.includes('fertil')) iconeTipo = 'bi-tree-fill text-flora-muted';
            else if (tipoFormatado.includes('poda')) iconeTipo = 'bi-scissors text-flora-muted';

            containerPendentes.innerHTML += `
                <div class="d-flex align-items-center justify-content-between bg-flora-black border border-warning border-opacity-50 p-2 rounded-2 shadow-sm">
                    <div class="d-flex align-items-center gap-3">
                        <i class="bi ${iconeTipo} fs-5"></i>
                        <div>
                            <span class="fw-bold small text-flora-main">${t.descricao}</span>
                            <div class="extra-small text-flora-muted"><i class="bi bi-arrow-repeat me-1"></i> Frequência: ${t.frequencia}</div>
                        </div>
                    </div>
                    <div class="d-flex align-items-center gap-2">
                        <button class="btn btn-outline-secondary border-flora-green text-flora-main btn-sm fw-bold py-0 px-2 text-small d-flex align-items-center gap-1" onclick="alternarConclusaoTarefa('${t.id}', '${setorPlantaId}')" title="Marcar como Concluída">
                            Concluir <i class="bi bi-check-lg text-flora-green"></i>
                        </button>
                        <button class="btn btn-outline-danger btn-sm border-0 py-0 px-1" onclick="deletarTarefa('${t.id}', '${setorPlantaId}')" title="Excluir tarefa">
                            <i class="bi bi-x fs-5"></i>
                        </button>
                    </div>
                </div>
            `;
        });
    }

    if (concluidas.length === 0) {
        containerConcluidas.innerHTML = `<div class="text-muted small py-1 fst-italic">Nenhuma tarefa concluída ainda.</div>`;
    } else {
        concluidas.forEach(t => {
            containerConcluidas.innerHTML += `
                <div class="d-flex align-items-center justify-content-between bg-flora-black border border-flora-green border-opacity-10 p-2 rounded-2 opacity-75">
                    <div class="d-flex align-items-center gap-3">
                        <i class="bi bi-check-circle-fill text-flora-green fs-5"></i>
                        <div>
                            <span class="fw-bold small text-decoration-line-through text-flora-muted">${t.descricao}</span>
                            <div class="extra-small text-flora-muted">Frequência: ${t.frequencia}</div>
                        </div>
                    </div>
                    <div class="d-flex align-items-center gap-2">
                        <button class="btn btn-outline-secondary btn-sm fw-bold py-0 px-2 text-small text-flora-muted d-flex align-items-center gap-1" onclick="alternarConclusaoTarefa('${t.id}', '${setorPlantaId}')" title="Desfazer">
                            Desfazer
                        </button>
                        <button class="btn btn-outline-danger btn-sm border-0 py-0 px-1" onclick="deletarTarefa('${t.id}', '${setorPlantaId}')" title="Excluir tarefa">
                            <i class="bi bi-x fs-5"></i>
                        </button>
                    </div>
                </div>
            `;
        });
    }
}

function abrirModalNovaTarefa(setorPlantaId) {
    document.getElementById('tarefaSetorPlantaId').value = setorPlantaId;
    document.getElementById('descricaoTarefa').value = '';
    document.getElementById('tipoTarefa').value = 'Rega';
    document.getElementById('frequenciaTarefa').value = 'Diariamente';
    document.getElementById('divPersonalizado').style.display = 'none';
    document.getElementById('detalhePersonalizado').value = '';

    const modalEl = document.getElementById('modalTarefa');
    const modal = new bootstrap.Modal(modalEl);
    modal.show();
}

async function salvarNovaTarefa(event) {
    event.preventDefault();

    const setorPlantaId = document.getElementById('tarefaSetorPlantaId').value;
    const descricao = document.getElementById('descricaoTarefa').value.trim();
    const tipo = document.getElementById('tipoTarefa').value;
    const freqSelect = document.getElementById('frequenciaTarefa').value;

    let frequencia = freqSelect;
    if (freqSelect === 'Personalizado') {
        const persVal = document.getElementById('detalhePersonalizado').value.trim();
        frequencia = persVal ? `A cada ${persVal} dias` : 'Personalizado';
    }

    try {
        const response = await fetch(`${API_BASE_URL}/${setorId}/plantas/${setorPlantaId}/tarefas`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ descricao, tipo, frequencia })
        });

        if (response.ok) {
            const modalEl = document.getElementById('modalTarefa');
            const modal = bootstrap.Modal.getInstance(modalEl);
            if (modal) modal.hide();

            carregarDetalhesESetorPlantas();
        } else {
            alert('Erro ao salvar tarefa.');
        }
    } catch (error) {
        console.error("Erro:", error);
    }
}

async function alternarConclusaoTarefa(tarefaId, setorPlantaId) {
    try {
        const response = await fetch(`${API_BASE_URL}/${setorId}/plantas/${setorPlantaId}/tarefas/${tarefaId}/concluir`, {
            method: 'PATCH'
        });
        if (response.ok) {
            carregarDetalhesESetorPlantas();
        }
    } catch (error) {
        console.error("Erro:", error);
    }
}

async function deletarTarefa(tarefaId, setorPlantaId) {
    try {
        const response = await fetch(`${API_BASE_URL}/${setorId}/plantas/${setorPlantaId}/tarefas/${tarefaId}`, {
            method: 'DELETE'
        });
        if (response.ok) {
            carregarDetalhesESetorPlantas();
        }
    } catch (error) {
        console.error("Erro:", error);
    }
}

async function carregarBibliotecaParaModal() {
    try {
        const response = await fetch(`http://localhost:8080/api/usuarios/${usuarioLogadoId}/biblioteca`);
        if (response.ok) {
            plantasBibliotecaCache = await response.json();
            renderizarListaBiblioteca(plantasBibliotecaCache);
        }
    } catch (error) {
        console.error("Erro ao carregar biblioteca:", error);
    }
}

function renderizarListaBiblioteca(plantas) {
    const listaEl = document.getElementById('listaBibliotecaModal');
    if (!listaEl) return;
    listaEl.innerHTML = '';

    if (!plantas || plantas.length === 0) {
        listaEl.innerHTML = `<p class="text-muted text-center small py-3">Nenhuma planta encontrada na biblioteca.</p>`;
        return;
    }

    plantas.forEach(planta => {
        listaEl.innerHTML += `
            <div class="d-flex align-items-center justify-content-between bg-flora-black border border-flora-green border-opacity-50 p-2 rounded-2 mb-2">
                <div class="d-flex align-items-center gap-2">
                    ${planta.fotoUrl ? `<img src="${planta.fotoUrl}" class="rounded-2" width="35" height="35" style="object-fit:cover;">` : '<i class="bi bi-flower1 text-flora-green fs-5"></i>'}
                    <div>
                        <span class="fw-bold text-flora-main small">${planta.nome || planta.nomePopular}</span>
                        <div class="extra-small text-flora-muted">${planta.nomeCientifico || ''}</div>
                    </div>
                </div>
                <button class="btn btn-flora-green btn-sm fw-bold" onclick="vincularPlanta('${planta.id}')">
                    Adicionar ao Setor
                </button>
            </div>
        `;
    });
}

function filtrarBibliotecaModal() {
    const inputEl = document.getElementById('inputFiltroBiblioteca');
    if (!inputEl) return;
    const termo = inputEl.value.toLowerCase().trim();

    if (!termo) {
        renderizarListaBiblioteca(plantasBibliotecaCache);
        return;
    }

    const filtradas = plantasBibliotecaCache.filter(p => {
        const nome = (p.nome || p.nomePopular || '').toLowerCase();
        const cientifico = p.nomeCientifico ? p.nomeCientifico.toLowerCase() : '';
        const categoria = p.categoria ? p.categoria.toLowerCase() : '';
        return nome.includes(termo) || cientifico.includes(termo) || categoria.includes(termo);
    });

    renderizarListaBiblioteca(filtradas);
}

async function vincularPlanta(plantaId) {
    const plantaBiblioteca = plantasBibliotecaCache.find(p => p.id === plantaId);
    if (!plantaBiblioteca) return;

    // Constrói o objeto do subdocumento Planta no Setor copiando os dados da biblioteca
    const dadosPlantaSetor = {
        plantaId: plantaBiblioteca.id,
        nome: plantaBiblioteca.nome || plantaBiblioteca.nomePopular,
        nomeCientifico: plantaBiblioteca.nomeCientifico,
        iluminacao: plantaBiblioteca.iluminacao,
        rega: plantaBiblioteca.rega,
        temperatura: plantaBiblioteca.temperatura,
        fotoUrl: plantaBiblioteca.fotoUrl
    };

    try {
        const response = await fetch(`${API_BASE_URL}/${setorId}/plantas`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dadosPlantaSetor)
        });

        if (response.ok) {
            const modalEl = document.getElementById('modalAdicionarPlanta');
            const modal = bootstrap.Modal.getInstance(modalEl);
            if (modal) modal.hide();
            carregarDetalhesESetorPlantas();
        } else {
            alert('Erro ao vincular planta ao setor.');
        }
    } catch (error) {
        console.error("Erro:", error);
    }
}

async function vincularPorCodigo() {
    const codigoInput = document.getElementById('codigoPartilhaInput');
    const codigo = codigoInput ? codigoInput.value.trim().toUpperCase() : '';

    if (!codigo) {
        alert('Por favor, insira um código válido.');
        return;
    }

    let plantaEncontrada = plantasBibliotecaCache.find(p => p.codigoPartilha && p.codigoPartilha.toUpperCase() === codigo || p.id === codigo);
    if (plantaEncontrada) {
        await vincularPlanta(plantaEncontrada.id);
        codigoInput.value = '';
        return;
    }
    alert('Nenhuma planta encontrada com este código na sua biblioteca.');
}

async function removerVinculo(vinculoId) {
    if (!confirm('Deseja realmente remover esta planta deste setor?')) return;
    try {
        const response = await fetch(`${API_BASE_URL}/${setorId}/plantas/${vinculoId}`, {
            method: 'DELETE'
        });
        if (response.ok) {
            carregarDetalhesESetorPlantas();
        } else {
            alert('Erro ao remover o vínculo.');
        }
    } catch (error) {
        console.error("Erro:", error);
    }
}