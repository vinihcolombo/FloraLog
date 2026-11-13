const usuarioLogado = JSON.parse(localStorage.getItem('usuarioLogado'));
const USUARIO_ID_LOGADO = usuarioLogado ? (usuarioLogado.id || usuarioLogado.usuarioId) : null;

const API_URL = `http://localhost:8080/api/usuarios/${USUARIO_ID_LOGADO}/setores`;

let setoresCache = [];

if (!USUARIO_ID_LOGADO) {
    window.location.href = 'index.html';
}

document.addEventListener('DOMContentLoaded', () => {
    if (document.getElementById('gridSetores') || document.getElementById('gridPlantas')) {
        carregarSetores();
    }

    const formSetor = document.getElementById('formSetor');
    if (formSetor) {
        formSetor.addEventListener('submit', function(event) {
            event.preventDefault();
            salvarSetor();
        });
    }

    carregarResumoDashboard();
});

async function carregarResumoDashboard() {
    try {
        const response = await fetch(`http://localhost:8080/api/usuarios/${USUARIO_ID_LOGADO}`);
        if (response.ok) {
            const usuario = await response.json();

            const valPendencias = document.getElementById('valPendencias');
            const valSetores = document.getElementById('valSetores');
            const valPlantas = document.getElementById('valPlantas');
            const listaTarefas = document.getElementById('listaTarefas');

            const totalBiblioteca = usuario.bibliotecaPlantas ? usuario.bibliotecaPlantas.length : 0;
            const totalSetores = usuario.setores ? usuario.setores.length : 0;

            let totalTarefasPendentes = 0;
            let todasPendencias = [];

            if (usuario.setores && Array.isArray(usuario.setores)) {
                usuario.setores.forEach(setor => {
                    if (setor.plantas && Array.isArray(setor.plantas)) {
                        setor.plantas.forEach(plantaSetor => {
                            if (plantaSetor.tarefas && Array.isArray(plantaSetor.tarefas)) {
                                plantaSetor.tarefas.forEach(tarefa => {
                                    if (!tarefa.concluida) {
                                        totalTarefasPendentes++;
                                        todasPendencias.push({
                                            tarefa,
                                            planta: plantaSetor,
                                            setor
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }

            if (valPendencias) valPendencias.innerText = totalTarefasPendentes;
            if (valSetores) valSetores.innerText = totalSetores;
            if (valPlantas) valPlantas.innerText = totalBiblioteca;

            if (listaTarefas) {
                if (todasPendencias.length > 0) {
                    const LIMITE_EXIBICAO = 3;
                    const pendenciasExibidas = todasPendencias.slice(0, LIMITE_EXIBICAO);

                    let htmlPendencias = '';
                    pendenciasExibidas.forEach(item => {
                        const idTarefa = item.tarefa.id || item.tarefa._id;
                        const idSetor = item.setor.id || item.setor._id;
                        const idPlanta = item.planta.id || item.planta._id;

                        htmlPendencias += `
                            <div class="list-group-item bg-flora-card border border-flora-green rounded p-3 d-flex justify-content-between align-items-center shadow-sm">
                                <div>
                                    <h6 class="mb-1 fw-bold text-flora-green">${item.tarefa.descricao || 'Tarefa'} (${item.tarefa.tipo || 'Geral'})</h6>
                                    <p class="mb-0 text-flora-muted small">Planta: <strong>${item.planta.nomePopular || item.planta.nome || 'Planta'}</strong> • Setor: <span class="text-warning">${item.setor.nome}</span></p>
                                </div>
                                <div class="d-flex gap-2">
                                    <button class="btn btn-sm btn-outline-success border-flora-green text-flora-green fw-bold" onclick="concluirTarefaDireto('${idSetor}', '${idPlanta}', '${idTarefa}')" title="Concluir Tarefa">
                                        <i class="bi bi-check-lg me-1"></i> Concluir
                                    </button>
                                    <a href="setor-detalhes.html?id=${idSetor}" class="btn btn-sm btn-outline-secondary text-flora-main border-flora-green" title="Ir para o Setor">
                                        <i class="bi bi-arrow-right-circle me-1"></i> Ir para o Setor
                                    </a>
                                </div>
                            </div>
                        `;
                    });

                    if (todasPendencias.length > LIMITE_EXIBICAO) {
                        htmlPendencias += `
                            <div class="text-center pt-2">
                                <a href="horta-casa.html" class="btn btn-sm btn-outline-success border-flora-green text-flora-green fw-bold">
                                    <i class="bi bi-three-dots me-1"></i> Ir para Horta da Casa (Ver todas)
                                </a>
                            </div>
                        `;
                    }

                    listaTarefas.innerHTML = htmlPendencias;
                } else {
                    listaTarefas.innerHTML = `
                        <div class="list-group-item bg-flora-card border border-flora-green rounded p-3 text-center text-flora-muted">
                            <p class="mb-0"><i class="bi bi-check-circle text-success me-2"></i>Parabéns! Não existem pendências de cuidados no momento.</p>
                        </div>
                    `;
                }
            }
        }
    } catch (error) {
        console.error('Erro ao carregar resumo do painel:', error);
    }
}

// Função para concluir a tarefa diretamente do painel principal
async function concluirTarefaDireto(setorId, plantaId, tarefaId) {
    try {
        const response = await fetch(`http://localhost:8080/api/usuarios/${USUARIO_ID_LOGADO}/setores/${setorId}/plantas/${plantaId}/tarefas/${tarefaId}/concluir`, {
            method: 'PATCH'
        });

        if (response.ok) {
            carregarResumoDashboard(); // Atualiza os contadores e a lista instantaneamente
        } else {
            alert('Erro ao concluir a tarefa.');
        }
    } catch (error) {
        console.error('Erro ao concluir tarefa:', error);
    }
}

async function carregarSetores() {
    try {
        const response = await fetch(API_URL);
        if (response.ok) {
            setoresCache = await response.json();
            renderizarSetores(setoresCache);
        }
    } catch (error) {
        console.error('Erro ao carregar setores:', error);
    }
}

function renderizarSetores(setores) {
    const grid = document.getElementById('gridSetores') || document.getElementById('gridPlantas');
    if (!grid) return;
    grid.innerHTML = '';

    if (!setores || setores.length === 0) {
        grid.innerHTML = `<div class="col-12 text-center text-secondary py-5"><p>Nenhum setor encontrado.</p></div>`;
        return;
    }

    setores.forEach(s => {
        const col = document.createElement('div');
        col.className = 'col';
        col.innerHTML = `
            <div class="card bg-flora-card border-flora-green h-100 shadow-sm">
                <div class="card-body">
                    <h4 class="card-title text-flora-main fw-bold mb-1">${s.nome || 'Setor'}</h4>
                    <p class="card-subtitle mb-3 text-secondary fst-italic">${s.descricao || 'Sem descrição.'}</p>
                    <p class="text-secondary small">Plantas no setor: ${s.plantas ? s.plantas.length : 0}</p>
                </div>
                <div class="card-footer bg-transparent border-top border-flora-green d-flex justify-content-between align-items-center py-2">
                    <a href="setor-detalhes.html?id=${s.id}" class="btn btn-sm btn-flora-green"><i class="bi bi-eye"></i> Detalhes</a>
                    <button class="btn btn-sm btn-outline-danger" onclick="confirmarExclusaoSetor('${s.id}')"><i class="bi bi-trash"></i> Excluir</button>
                </div>
            </div>
        `;
        grid.appendChild(col);
    });
}

async function salvarSetor() {
    const nome = document.getElementById('nomeSetor').value;
    const descricao = document.getElementById('descricaoSetor').value;

    const setorData = { nome, descricao };

    try {
        const response = await fetch(API_URL, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(setorData)
        });

        if (response.ok) {
            const modalElement = document.getElementById('modalSetor');
            if (modalElement) {
                const modal = bootstrap.Modal.getInstance(modalElement);
                if (modal) modal.hide();
            }
            carregarSetores();
            carregarResumoDashboard();
        } else {
            alert('Erro ao salvar setor.');
        }
    } catch (error) {
        console.error('Erro ao salvar setor:', error);
    }
}

async function confirmarExclusaoSetor(id) {
    if (confirm("Tem a certeza de que deseja excluir este setor?")) {
        try {
            const response = await fetch(`${API_URL}/${id}`, { method: 'DELETE' });
            if (response.ok) {
                carregarSetores();
                carregarResumoDashboard();
            }
        } catch (error) {
            console.error('Erro ao excluir setor:', error);
        }
    }
}