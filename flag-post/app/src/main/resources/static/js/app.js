const theme = {
    extend: {
        colors: {
            background: '#000000',
            primary: '#00FF87',
            secondary: '#1E1E1E',
            accent: '#00BFFF',
            error: '#FF3131',
            success: '#00FF00',
            text: '#CCCCCC',
        },
    },
    hacker: {
        colors: {
            green1: '#61cf5a',
            green2: '#63ad58',
            green3: '#50864c',
            green4: '#3e6a3d',
            green5: '#3b4b33'
        }
    }
}

async function login() {
    const email = document.getElementById('email').value
    const password = document.getElementById('password').value
    const headers = { 'Content-Type': 'application/json' }
    const body = JSON.stringify({ email, password })
    try {
        const result = await fetch('/user/token', { method: 'post', body, headers })
        if (result.status === 200) {
            location.href = `${location.protocol}//${location.host}`
            return
        }

        if (result.status === 400 || result.status === 401) {
            showError(await result.text())
            return
        }
        console.log(await result.json())
    } catch (ex) {
        console.log(ex)
    }
}

async function create() {
    const nickname = document.getElementById('nickname').value
    const email = document.getElementById('email').value
    const password = document.getElementById('password').value
    const confirm = document.getElementById('confirm').value

    const headers = { 'Content-Type': 'application/json' }
    const body = JSON.stringify({ email, password, nickname, confirm })

    try {
        const result = await fetch('/user/create', { method: 'post', body, headers })

        if (result.status === 200) {
            location.href = `${location.protocol}//${location.host}/login.html`
            return
        }

        if (result.status === 400 || result.status === 401) {
            showError(await result.text())
            return
        }

        showError('Erro desconhecido.')

    } catch (ex) {
        console.log('TRY CATCH', ex)
    }
}

async function getUserData() {
    try {
        const result = await fetch('/user/data')
        if (result.status === 200)
            return await result.json()
    } catch (ex) {
        console.log('TRY CATCH', ex)
    }
}

async function logout() {
    location.href = `${location.protocol}//${location.host}/user/logout`
}

function showError(text) {
    const messageErrorElem = document.getElementById('messageError')
    messageErrorElem.innerText = text
    messageErrorElem.style.height = '20px'
    setTimeout(() => {
        messageErrorElem.innerText = ''
        messageErrorElem.style.height = '0'
    }, 3000);
}
