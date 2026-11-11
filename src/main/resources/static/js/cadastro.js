document.getElementById('formCadastro').addEventListener('submit', async function(event) {
    event.preventDefault();

    const nome = document.getElementById('nome').value;
    const email = document.getElementById('email').value;
    const senha = document.getElementById('senha').value;
    const confirmarSenha = document.getElementById('confirmarSenha').value;
    const mensagemDiv = document.getElementById('mensagem');

    if (senha !== confirmarSenha) {
        mensagemDiv.className = 'alert alert-danger py-2 small';
        mensagemDiv.innerText = 'As senhas não coincidem!';
        mensagemDiv.classList.remove('d-none');
        return;
    }

    const usuarioData = {
        nome: nome,
        email: email,
        senha: senha,
        avatarUrl: 'https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/icons/person-circle.svg',
        perfil: 'COLABORADOR'
    };

    try {
        const response = await fetch('http://localhost:8080/api/usuarios', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(usuarioData)
        });

        if (response.ok) {
            mensagemDiv.className = 'alert alert-success py-2 small';
            mensagemDiv.innerText = 'Conta criada com sucesso! Redirecionando...';
            mensagemDiv.classList.remove('d-none');

            setTimeout(() => {
                window.location.href = 'login.html';
            }, 2000);
        } else {
            const erroTexto = await response.text();
            mensagemDiv.className = 'alert alert-danger py-2 small';
            mensagemDiv.innerText = 'Erro ao cadastrar: ' + (erroTexto || 'Verifique os dados.');
            mensagemDiv.classList.remove('d-none');
        }
    } catch (error) {
        console.error('Erro na requisição:', error);
        mensagemDiv.className = 'alert alert-warning py-2 small';
        mensagemDiv.innerText = 'Erro de conexão com o servidor backend.';
        mensagemDiv.classList.remove('d-none');
    }
});