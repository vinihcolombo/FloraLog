document.addEventListener('DOMContentLoaded', () => {
    const sidebar = document.getElementById('sidebar');
    const btnToggle = document.getElementById('btnToggleSidebar');

    if (!sidebar) return;

    const estadoSalvo = localStorage.getItem('sidebarMinimizada') === 'true';
    if (estadoSalvo) {
        sidebar.classList.add('sidebar-minimizada');
    }

    document.documentElement.classList.remove('sidebar-pre-minimizada');

    if (btnToggle) {
        const novoBotao = btnToggle.cloneNode(true);
        btnToggle.parentNode.replaceChild(novoBotao, btnToggle);

        novoBotao.addEventListener('click', (e) => {
            e.preventDefault();
            sidebar.classList.toggle('sidebar-minimizada');

            const estaMinimizada = sidebar.classList.contains('sidebar-minimizada');
            localStorage.setItem('sidebarMinimizada', estaMinimizada);
        });
    }
});