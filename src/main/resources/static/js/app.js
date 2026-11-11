const API_URL = '/api/plantas';
let plantasCache = [];

document.addEventListener('DOMContentLoaded', () => {
   carregarPlantas();

   const formPlanta = document.getElementById('formPlanta');
   if (formPlanta) {
       formPlanta.addEventListener('submit', function(event) {
           event.preventDefault();

           const id = document.getElementById('plantaId').value;
           const acaoTexto = id ? "atualizar e guardar as alterações desta planta" : "cadastrar esta nova planta";

           mostrarConfirmacao(
               id ? "Salvar Alterações" : "Cadastrar Espécie",
               `Tem a certeza de que deseja ${acaoTexto}?`,
               function() {
                   executarSalvamentoPlanta();
               }
           );
       });
   }

   fetch('http://localhost:8080/api/dashboard/resumo')
       .then(response => response.json())
       .then(data => {
           const valHortas = document.getElementById('valHortas');
           const valLotes = document.getElementById('valLotes');
           const valPlantas = document.getElementById('valPlantas');

           if (valHortas) valHortas.innerText = data.totalHortas || 0;
           if (valLotes) valLotes.innerText = data.totalLotes || 0;
           if (valPlantas) valPlantas.innerText = data.totalPlantas || 0;
       })
       .catch(error => console.error('Erro ao carregar resumo:', error));
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
   if (!grid) return;
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
                   <h4 class="card-title text-flora-main fw-bold mb-1">${p.nomePopular || p.nome || ''}</h4>
                   <h6 class="card-subtitle mb-3 text-secondary fst-italic">${p.nomeCientifico || ''}</h6>

                   <ul class="list-unstyled text-secondary small mb-3">
                       <li class="mb-1"><i class="bi bi-sun text-flora-green me-2"><strong>Luz:</strong></i> ${p.iluminacao || 'N/A'}</li>
                       <li class="mb-1"><i class="bi bi-droplet text-flora-green me-2"><strong>Rega:</strong></i> ${p.rega || 'N/A'}</li>
                       <li class="mb-1"><i class="bi bi-thermometer-half text-flora-green me-2"><strong>Temp:</strong></i> ${p.temperatura || 'N/A'}</li>
                   </ul>

                   <p class="card-text text-secondary small border-top border-secondary pt-2 mt-2">
                       ${p.descricao || 'Sem orientações adicionais.'}
                   </p>
               </div>
               <div class="card-footer bg-transparent border-top border-flora-green d-flex justify-content-end gap-2 py-2">
                   <button class="btn btn-sm btn-flora-outline" onclick="prepararEdicao('${p.id}')"><i class="bi bi-pencil"></i> Editar</button>
                   <button class="btn btn-sm btn-outline-danger" onclick="confirmarExclusao('${p.id}')"><i class="bi bi-trash"></i> Excluir</button>
               </div>
           </div>
       `;
       grid.appendChild(col);
   });
}

function toggleModalSelect() {
    const opcoes = document.getElementById('opcoesModalDificuldade');
    if (opcoes) opcoes.style.display = opcoes.style.display === 'block' ? 'none' : 'block';
}

function escolherModalDificuldade(valor, texto) {
    document.getElementById('nivelDificuldade').value = valor;
    document.getElementById('textoModalDificuldadeSelecionada').innerText = texto;
    const opcoes = document.getElementById('opcoesModalDificuldade');
    if (opcoes) opcoes.style.display = 'none';
}

window.addEventListener('click', function(e) {
    const boxModal = document.getElementById('boxModalDificuldade');
    const opcoes = document.getElementById('opcoesModalDificuldade');
    if (boxModal && !boxModal.contains(e.target) && opcoes) {
        opcoes.style.display = 'none';
    }
});

function prepararEdicao(id) {
   const planta = plantasCache.find(p => p.id === id);
   if (!planta) return;

   const dif = planta.nivelDificuldade || 'Fácil';
   document.getElementById('nivelDificuldade').value = dif;
   document.getElementById('textoModalDificuldadeSelecionada').innerText = dif;

   document.getElementById('plantaId').value = planta.id;
   document.getElementById('nomePopular').value = planta.nomePopular || planta.nome || '';
   document.getElementById('nomeCientifico').value = planta.nomeCientifico || '';
   document.getElementById('categoria').value = planta.categoria || '';
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

function toggleCustomSelect() {
    const opcoes = document.getElementById('opcoesDificuldade');
    if (opcoes) opcoes.style.display = opcoes.style.display === 'block' ? 'none' : 'block';
}

function escolherDificuldade(valor, texto) {
    document.getElementById('selectDificuldade').value = valor;
    document.getElementById('textoDificuldadeSelecionada').innerText = texto;
    const opcoes = document.getElementById('opcoesDificuldade');
    if (opcoes) opcoes.style.display = 'none';
    filtrarPlantas();
}

window.addEventListener('click', function(e) {
    const box = document.getElementById('boxDificuldade');
    const opcoes = document.getElementById('opcoesDificuldade');
    if (box && !box.contains(e.target) && opcoes) {
        opcoes.style.display = 'none';
    }
});

function filtrarPlantas() {
   const buscaEl = document.getElementById('inputBusca');
   const difEl = document.getElementById('selectDificuldade');

   const busca = buscaEl ? buscaEl.value.toLowerCase() : '';
   const dificuldade = difEl ? difEl.value : '';

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

function mostrarConfirmacao(titulo, mensagem, onConfirmar) {
    const tituloEl = document.getElementById('tituloModalConfirmacao');
    const msgEl = document.getElementById('mensagemModalConfirmacao');
    if (tituloEl) tituloEl.innerHTML = `<i class="bi bi-exclamation-triangle-fill me-2"></i>${titulo}`;
    if (msgEl) msgEl.innerText = mensagem;

    const btnConfirmar = document.getElementById('btnConfirmarAcao');
    if (!btnConfirmar) return;

    const novoBtn = btnConfirmar.cloneNode(true);
    btnConfirmar.parentNode.replaceChild(novoBtn, btnConfirmar);

    const modalConfirmacaoEl = document.getElementById('modalConfirmacao');
    if (!modalConfirmacaoEl) return;

    const modalConfirmacaoBootstrap = bootstrap.Modal.getOrCreateInstance(modalConfirmacaoEl);

    document.getElementById('btnConfirmarAcao').addEventListener('click', function() {
        modalConfirmacaoBootstrap.hide();

        setTimeout(() => {
            document.querySelectorAll('.modal-backdrop').forEach(el => el.remove());
            document.body.classList.remove('modal-open');
            document.body.style.overflow = '';
            document.body.style.paddingRight = '';
        }, 200);

        if (typeof onConfirmar === 'function') {
            onConfirmar();
        }
    });

    modalConfirmacaoBootstrap.show();
}

async function executarSalvamentoPlanta() {
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

function confirmarExclusao(id) {
    mostrarConfirmacao(
        "Excluir Espécie",
        "Tem a certeza de que deseja excluir esta planta?",
        function() {
            executarExclusaoPlanta(id);
        }
    );
}

async function executarExclusaoPlanta(id) {
   try {
       const response = await fetch(`${API_URL}/${id}`, { method: 'DELETE' });
       if (response.ok) {
           carregarPlantas();
       }
   } catch (error) {
       console.error('Erro ao excluir:', error);
   }
}

function limparFormulario() {
   const nivelDif = document.getElementById('nivelDificuldade');
   const textoModal = document.getElementById('textoModalDificuldadeSelecionada');
   const form = document.getElementById('formPlanta');
   const plantaId = document.getElementById('plantaId');
   const titulo = document.getElementById('modalTitulo');

   if (nivelDif) nivelDif.value = 'Fácil';
   if (textoModal) textoModal.innerText = 'Fácil';
   if (form) form.reset();
   if (plantaId) plantaId.value = '';
   if (titulo) titulo.innerHTML = '<i class="bi bi-flower2 me-2"></i>Cadastrar Espécie';
}