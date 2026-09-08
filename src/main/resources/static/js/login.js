document.getElementById('formLogin').addEventListener('submit', async function(event) {
    event.preventDefault();

    const email = document.getElementById('email').value;
    const senha = document.getElementById('senha').value;
    const mensagemDiv = document.getElementById('mensagem');

    const loginData = {
        email: email,
        senha: senha
    };

    try {
        const response = await fetch('http://localhost:8080/api/usuarios/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(loginData)
        });

        if (response.ok) {
            mensagemDiv.className = 'alert alert-success py-2 small';
            mensagemDiv.innerText = 'Login realizado com sucesso! Entrando...';
            mensagemDiv.classList.remove('d-none');
            
            setTimeout(() => {
                window.location.href = '/app.html';
            }, 1500);
        } else {
            mensagemDiv.className = 'alert alert-danger py-2 small';
            mensagemDiv.innerText = 'E-mail ou senha inválidos.';
            mensagemDiv.classList.remove('d-none');
        }
    } catch (error) {
        console.error('Erro na requisição:', error);
        mensagemDiv.className = 'alert alert-warning py-2 small';
        mensagemDiv.innerText = 'Modo de visualização (Backend offline).';
        mensagemDiv.classList.remove('d-none');
    }
});