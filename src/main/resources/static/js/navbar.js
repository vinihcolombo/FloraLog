document.addEventListener("DOMContentLoaded", () => {

    const usuarioSalvo = localStorage.getItem('usuarioLogado');
    if (!usuarioSalvo) {
        window.location.href = 'login.html';
        return;
    }

    try {
        const usuario = JSON.parse(usuarioSalvo);

        const userName = document.getElementById('userName');
        if (userName) {
            userName.innerText = usuario.nome || 'Utilizador';
        }

        const userRole = document.getElementById('userRole');
        if (userRole) {
            userRole.innerText = usuario.perfil || usuario.cargo || 'Colaborador';
        }

        const userAvatar = document.getElementById('userAvatar');
        if (userAvatar) {
            const avatarSrc = usuario.avatarUrl || usuario.fotoUrl;

            if (userAvatar.tagName === 'IMG') {
                if (avatarSrc) {
                    userAvatar.src = avatarSrc;
                }
            } else {
                if (avatarSrc) {
                    userAvatar.innerHTML = `<img src="${avatarSrc}" alt="Avatar" class="w-100 h-100 object-fit-cover rounded-circle">`;
                } else {

                    userAvatar.innerHTML = `<i class="bi bi-person-circle fs-3 flora-user-icon"></i>`;
                }
            }
        }
    } catch (e) {
        console.error('Erro ao ler dados do utilizador:', e);
    }

    const btnLogout = document.getElementById('btnLogout');
    if (btnLogout) {
        btnLogout.addEventListener('click', (e) => {
            e.preventDefault();
            localStorage.removeItem('usuarioSalvo');
            window.location.href = 'login.html';
        });
    }
});