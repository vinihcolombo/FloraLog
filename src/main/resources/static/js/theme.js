document.addEventListener('DOMContentLoaded', () => {
    const temaSalvo = localStorage.getItem('temaPreferido') || 'dark';
    document.documentElement.setAttribute('data-bs-theme', temaSalvo);
    atualizarIconeBotao(temaSalvo);
});

function alternarTema() {
    const htmlElement = document.documentElement;
    const temaAtual = htmlElement.getAttribute('data-bs-theme');
    const novoTema = temaAtual === 'dark' ? 'light' : 'dark';
    
    htmlElement.setAttribute('data-bs-theme', novoTema);
    localStorage.setItem('temaPreferido', novoTema);
    atualizarIconeBotao(novoTema);
}

function atualizarIconeBotao(tema) {
    const icone = document.getElementById('iconeTema');
    if (!icone) return;

    if (tema === 'light') {
        icone.className = 'bi bi-moon-fill';
    } else {
        icone.className = 'bi bi-sun-fill';
    }
}