// Gerenciador de Tema (Claro / Escuro)
document.addEventListener('DOMContentLoaded', () => {
    // Recupera o tema salvo ou define 'dark' como padrão
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
        icone.className = 'bi bi-moon-fill'; // Mostra lua no modo claro para indicar que pode mudar para escuro
    } else {
        icone.className = 'bi bi-sun-fill';  // Mostra sol no modo escuro para indicar que pode mudar para claro
    }
}