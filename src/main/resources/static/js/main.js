function main() {
    fetch('../logout', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        },
    }).then(res => res.json())
    setTimeout(function () {
        window.location.href = "../login.html";
    }, 1000);
}

fetch("basic_info", {
    method: 'GET',
    headers: {
        'Content-Type': 'application/json'
    },
}).then(res => res.json())
    .then(data => {
        $("#username_label").text(data.data.uname);
    });