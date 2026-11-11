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

let plantasBibliotecaCache = [];

document.addEventListener("DOMContentLoaded", () => {
    if (!setorId || !usuarioLogadoId) {
        alert('Parâmetros inválidos ou sessão expirada.');
        window.location.href = 'horta-casa.html';
        return;
    }
    carregarDetalhesSetor();
    carregarPlantasDoSetor();
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

async function carregarDetalhesSetor() {
    try {
        const response = await fetch(`/api/setores/${setorId}`);
        if (response.ok) {
            const setor = await response.json();
            document.getElementById('nomeSetorTitulo').innerHTML = `<i class="bi bi-flower2 text-flora-green me-2"></i> ${setor.nome}`;
            document.getElementById('descricaoSetorTexto').textContent = setor.descricao || 'Sem descrição informada.';
        }
    } catch (error) {
        console.error("Erro ao carregar setor:", error);
    }
}

async function carregarPlantasDoSetor() {
    try {
        const responseVinculos = await fetch(`/api/setor-plantas?setorId=${setorId}`);
        if (!responseVinculos.ok) return;
        const vinculos = await responseVinculos.json();

        document.getElementById('totalPlantasSetor').textContent = vinculos.length;
        const grid = document.getElementById('gridPlantasSetor');
        grid.innerHTML = '';

        if (vinculos.length === 0) {
            grid.innerHTML = `<div class="text-center py-4 text-flora-muted card bg-flora-dark border border-flora-green p-4 rounded-3">Nenhuma planta neste setor ainda. Clique em "Adicionar da Biblioteca".</div>`;
            document.getElementById('totalRegasPendentes').textContent = '0';
            return;
        }

        for (const vinculo of vinculos) {
            const resPlanta = await fetch(`/api/biblioteca/${vinculo.plantaId}`);
            if (resPlanta.ok) {
                const planta = await resPlanta.json();

                let visualMedia = '';
                if (planta.fotoUrl && planta.fotoUrl.trim() !== '') {
                    visualMedia = `<img src="${planta.fotoUrl}" alt="${planta.nome}" class="rounded-3 border border-flora-green" width="70" height="70" style="object-fit: cover;">`;
                } else {
                    visualMedia = `
                        <div class="rounded-3 bg-flora-black border border-flora-green d-flex align-items-center justify-content-center" style="width: 70px; height: 70px; min-width: 70px;">
                            <i class="bi bi-flower1 fs-3 text-flora-green"></i>
                        </div>`;
                }

                grid.innerHTML += `
                    <div class="card bg-flora-dark border border-flora-green shadow-sm rounded-3 p-3">
                        <div class="d-flex align-items-start justify-content-between flex-wrap gap-3">
                            <div class="d-flex align-items-start gap-3">
                                ${visualMedia}
                                <div>
                                    <h4 class="fw-bold text-flora-main m-0">${planta.nome}</h4>
                                    <span class="text-flora-muted small fst-italic d-block mb-2">${planta.nomeCientifico || 'Espécie não informada'}</span>

                                    <div class="d-flex flex-wrap gap-3 text-small text-flora-muted">
                                        <span><i class="bi bi-sun text-warning me-1"></i> Luz: ${planta.iluminacao || 'N/A'}</span>
                                        <span><i class="bi bi-droplet text-info me-1"></i> Rega: ${planta.rega || 'N/A'}</span>
                                        <span><i class="bi bi-thermometer-half text-danger me-1"></i> Temp: ${planta.temperatura || 'N/A'}</span>
                                    </div>
                                </div>
                            </div>
                            <div class="d-flex align-items-center gap-2">
                                <button class="btn btn-outline-danger btn-sm" onclick="removerVinculo('${vinculo.id}')" title="Remover do Setor">
                                    <i class="bi bi-trash"></i>
                                </button>
                                <button class="btn btn-outline-secondary border-flora-green text-flora-main btn-sm d-flex align-items-center gap-1" type="button" data-bs-toggle="collapse" data-bs-target="#collapseTarefa-${vinculo.id}" aria-expanded="false" onclick="carregarTarefas('${vinculo.id}')">
                                    <i class="bi bi-chevron-down"></i> Tarefas
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
                                    <div class="text-muted small py-1">Carregando afazeres...</div>
                                </div>
                            </div>

                            <!-- Seção de Tarefas Concluídas -->
                            <div>
                                <span class="text-flora-green small fw-bold text-uppercase d-block mb-2"><i class="bi bi-check2-all me-1"></i> Concluídas</span>
                                <div id="listaConcluidas-${vinculo.id}" class="d-flex flex-column gap-2">
                                    <div class="text-muted small py-1">Nenhuma tarefa concluída.</div>
                                </div>
                            </div>
                        </div>
                    </div>
                `;
            }
        }

        atualizarContadorGeralPendentes(vinculos);

    } catch (error) {
        console.error("Erro ao carregar plantas do setor:", error);
    }
}

async function atualizarContadorGeralPendentes(vinculos) {
    let totalPendentes = 0;
    try {
        for (const vinculo of vinculos) {
            const response = await fetch(`/api/tarefas?setorPlantaId=${vinculo.id}`);
            if (response.ok) {
                const tarefas = await response.json();
                const pendentes = tarefas.filter(t => !t.concluida);
                totalPendentes += pendentes.length;
            }
        }
        document.getElementById('totalRegasPendentes').textContent = totalPendentes;
    } catch (error) {
        console.error("Erro ao calcular total de pendentes:", error);
    }
}

async function carregarTarefas(setorPlantaId) {
    const containerPendentes = document.getElementById(`listaPendentes-${setorPlantaId}`);
    const containerConcluidas = document.getElementById(`listaConcluidas-${setorPlantaId}`);

    try {
        const response = await fetch(`/api/tarefas?setorPlantaId=${setorPlantaId}`);
        if (response.ok) {
            const tarefas = await response.json();

            containerPendentes.innerHTML = '';
            containerConcluidas.innerHTML = '';

            const pendentes = tarefas.filter(t => !t.concluida);
            const concluidas = tarefas.filter(t => t.concluida);

            if (pendentes.length === 0) {
                containerPendentes.innerHTML = `<div class="text-muted small py-1 fst-italic">Nenhum afazer pendente no momento. Tudo em dia! 🌱</div>`;
            } else {
                pendentes.forEach(t => {
                    let iconeTipo = 'bi-list-task text-flora-muted';
                    const tipoFormatado = (t.tipo || '').toLowerCase();

                    if (tipoFormatado.includes('rega')) {
                        iconeTipo = 'bi-droplet-fill text-info';
                    } else if (tipoFormatado.includes('adub') || tipoFormatado.includes('fertil')) {
                        iconeTipo = 'bi-tree-fill text-flora-muted';
                    } else if (tipoFormatado.includes('poda')) {
                        iconeTipo = 'bi-scissors text-flora-muted';
                    }

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
    } catch (error) {
        console.error("Erro ao carregar tarefas:", error);
        containerPendentes.innerHTML = `<div class="text-danger small py-2">Erro ao carregar tarefas.</div>`;
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
        const response = await fetch('/api/tarefas', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                setorPlantaId: setorPlantaId,
                usuarioId: usuarioLogadoId,
                descricao: descricao,
                tipo: tipo,
                frequencia: frequencia
            })
        });

        if (response.ok) {
            const modalEl = document.getElementById('modalTarefa');
            const modal = bootstrap.Modal.getInstance(modalEl);
            if (modal) modal.hide();

            carregarTarefas(setorPlantaId);
            carregarPlantasDoSetor();
        } else {
            alert('Erro ao salvar tarefa.');
        }
    } catch (error) {
        console.error("Erro:", error);
    }
}

async function alternarConclusaoTarefa(tarefaId, setorPlantaId) {
    try {
        const response = await fetch(`/api/tarefas/${tarefaId}/concluir`, {
            method: 'PATCH'
        });
        if (response.ok) {
            carregarTarefas(setorPlantaId);
            carregarPlantasDoSetor();
        }
    } catch (error) {
        console.error("Erro:", error);
    }
}

async function deletarTarefa(tarefaId, setorPlantaId) {
    try {
        const response = await fetch(`/api/tarefas/${tarefaId}`, {
            method: 'DELETE'
        });
        if (response.ok) {
            carregarTarefas(setorPlantaId);
            carregarPlantasDoSetor();
        }
    } catch (error) {
        console.error("Erro:", error);
    }
}

async function carregarBibliotecaParaModal() {
    try {
        const response = await fetch(`/api/biblioteca/usuario/${usuarioLogadoId}`);
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
                        <span class="fw-bold text-flora-main small">${planta.nome}</span>
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
        const nome = p.nome ? p.nome.toLowerCase() : '';
        const cientifico = p.nomeCientifico ? p.nomeCientifico.toLowerCase() : '';
        const categoria = p.categoria ? p.categoria.toLowerCase() : '';
        return nome.includes(termo) || cientifico.includes(termo) || categoria.includes(termo);
    });

    renderizarListaBiblioteca(filtradas);
}

async function vincularPlanta(plantaId) {
    try {
        const response = await fetch('/api/setor-plantas', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                setorId: setorId,
                plantaId: plantaId,
                usuarioId: usuarioLogadoId
            })
        });

        if (response.ok) {
            const modalEl = document.getElementById('modalAdicionarPlanta');
            const modal = bootstrap.Modal.getInstance(modalEl);
            if (modal) modal.hide();
            carregarPlantasDoSetor();
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
        const response = await fetch(`/api/setor-plantas/${vinculoId}`, {
            method: 'DELETE'
        });
        if (response.ok) {
            carregarPlantasDoSetor();
        } else {
            alert('Erro ao remover o vínculo.');
        }
    } catch (error) {
        console.error("Erro:", error);
    }
}