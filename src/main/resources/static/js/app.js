const API_URL = '/api/plantas';
let plantasCache = []; 

document.addEventListener('DOMContentLoaded', () => {
    carregarPlantas();
    document.getElementById('formPlanta').addEventListener('submit', salvarPlanta);
});

async function carregarPlantas() {
    try {
        const response = await fetch(API_URL);
        if (response.ok) {
            plantasCache = await response.json();
            renderizarCards(plantasCache);
        }
    } catch (error) {
        console.error('Erro ao carregar plantas:', error);
    }
}

function renderizarCards(plantas) {
    const grid = document.getElementById('gridPlantas');
    grid.innerHTML = '';

    if (!plantas || plantas.length === 0) {
        grid.innerHTML = `<div class="col-12 text-center text-secondary py-5"><p>Nenhuma planta encontrada.</p></div>`;
        return;
    }

    plantas.forEach(p => {
        const col = document.createElement('div');
        col.className = 'col';
        col.innerHTML = `
            <div class="card bg-flora-card border-flora-green h-100 shadow-sm">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center mb-2">
                        <span class="badge badge-flora">${p.categoria || 'Geral'}</span>
                        <span class="badge bg-success-subtle text-success border border-success-subtle">${p.nivelDificuldade || 'Fácil'}</span>
                    </div>
                    <h4 class="card-title text-light fw-bold mb-1">${p.nomePopular || p.nome || ''}</h4>
                    <h6 class="card-subtitle mb-3 text-secondary fst-italic">${p.nomeCientifico || ''}</h6>

                    <ul class="list-unstyled text-secondary small mb-3">
                        <li class="mb-1"><i class="bi bi-sun text-flora-green me-2"></i><strong>Luz:</strong> ${p.iluminacao || 'N/A'}</li>
                        <li class="mb-1"><i class="bi bi-droplet text-flora-green me-2"></i><strong>Rega:</strong> ${p.rega || 'N/A'}</li>
                        <li class="mb-1"><i class="bi bi-thermometer-half text-flora-green me-2"></i><strong>Temp:</strong> ${p.temperatura || 'N/A'}</li>
                    </ul>

                    <p class="card-text text-secondary small border-top border-secondary pt-2 mt-2">
                        ${p.descricao || 'Sem orientações adicionais.'}
                    </p>
                </div>
                <div class="card-footer bg-transparent border-top border-flora-green d-flex justify-content-end gap-2 py-2">
                    <button class="btn btn-sm btn-flora-outline" onclick="prepararEdicao('${p.id}')"><i class="bi bi-pencil"></i> Editar</button>
                    <button class="btn btn-sm btn-outline-danger" onclick="excluirPlanta('${p.id}')"><i class="bi bi-trash"></i> Excluir</button>
                </div>
            </div>
        `;
        grid.appendChild(col);
    });
}

function prepararEdicao(id) {
    const planta = plantasCache.find(p => p.id === id);
    if (!planta) return;

    document.getElementById('plantaId').value = planta.id;
    document.getElementById('nomePopular').value = planta.nomePopular || planta.nome || '';
    document.getElementById('nomeCientifico').value = planta.nomeCientifico || '';
    document.getElementById('categoria').value = planta.categoria || '';
    document.getElementById('nivelDificuldade').value = planta.nivelDificuldade || 'Fácil';
    document.getElementById('iluminacao').value = planta.iluminacao || '';
    document.getElementById('rega').value = planta.rega || '';
    document.getElementById('temperatura').value = planta.temperatura || '';
    document.getElementById('descricao').value = planta.descricao || '';

    const titulo = document.getElementById('modalTitulo');
    if (titulo) titulo.innerHTML = '<i class="bi bi-pencil me-2"></i>Editar Espécie';

    const modalElement = document.getElementById('modalPlanta');
    const modal = new bootstrap.Modal(modalElement);
    modal.show();
}

function filtrarPlantas() {
    const busca = document.getElementById('inputBusca').value.toLowerCase();
    const dificuldade = document.getElementById('selectDificuldade').value;

    const filtradas = plantasCache.filter(p => {
        const nomeParaBusca = p.nomePopular || p.nome || '';
        const atendeBusca =
            nomeParaBusca.toLowerCase().includes(busca) ||
            (p.nomeCientifico && p.nomeCientifico.toLowerCase().includes(busca)) ||
            (p.categoria && p.categoria.toLowerCase().includes(busca));

        const atendeDificuldade = !dificuldade || p.nivelDificuldade === dificuldade;

        return atendeBusca && atendeDificuldade;
    });

    renderizarCards(filtradas);
}

async function salvarPlanta(event) {
    event.preventDefault();

    const id = document.getElementById('plantaId').value;

    const plantaData = {
        nome: document.getElementById('nomePopular').value,
        nomeCientifico: document.getElementById('nomeCientifico').value,
        categoria: document.getElementById('categoria').value,
        nivelDificuldade: document.getElementById('nivelDificuldade').value,
        iluminacao: document.getElementById('iluminacao').value,
        rega: document.getElementById('rega').value,
        temperatura: document.getElementById('temperatura').value,
        descricao: document.getElementById('descricao').value
    };

    const metodo = id ? 'PUT' : 'POST';
    const url = id ? `${API_URL}/${id}` : API_URL;

    try {
        const response = await fetch(url, {
            method: metodo,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(plantaData)
        });

        if (response.ok) {
            const modalElement = document.getElementById('modalPlanta');
            const modal = bootstrap.Modal.getInstance(modalElement);
            if (modal) modal.hide();

            limparFormulario();
            carregarPlantas();
        } else {
            alert('Erro ao salvar. Verifique se preencheu todos os campos obrigatórios.');
        }
    } catch (error) {
        console.error('Erro ao salvar:', error);
    }
}

async function excluirPlanta(id) {
    if (confirm('Tem certeza que deseja excluir esta planta?')) {
        try {
            const response = await fetch(`${API_URL}/${id}`, { method: 'DELETE' });
            if (response.ok) {
                carregarPlantas();
            }
        } catch (error) {
            console.error('Erro ao excluir:', error);
        }
    }
}

function limparFormulario() {
    document.getElementById('formPlanta').reset();
    document.getElementById('plantaId').value = '';
    const titulo = document.getElementById('modalTitulo');
    if (titulo) titulo.innerHTML = '<i class="bi bi-flower2 me-2"></i>Cadastrar Espécie';
}