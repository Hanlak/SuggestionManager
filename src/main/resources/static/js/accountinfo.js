// JavaScript functions

function editFullName() {
    document.getElementById('nameSection').style.display = 'none';
    document.getElementById('editNameSection').style.display = 'block';
    console.log("in edit full name");
}

function editEmail() {
    document.getElementById('emailSection').style.display = 'none';
    document.getElementById('editEmailSection').style.display = 'block';
    console.log("in edit email");
}

function submitEditFullName() {
    var editedName = document.getElementById('editNameInput').value;
    document.getElementById('name').innerText = editedName;
    document.getElementById('nameSection').style.display = 'block';
    document.getElementById('editNameSection').style.display = 'none';
    console.log(editedName);
    post_to_url("/editFullName", {
        fullName: editedName
    }, "POST");
}

function submitEditEmail() {
    var editedEmail = document.getElementById('editEmailInput').value;
    document.getElementById('email').innerText = editedEmail;
    document.getElementById('emailSection').style.display = 'block';
    document.getElementById('editEmailSection').style.display = 'none';
    console.log(editedEmail);
    post_to_url("/editEmail", {
        email: editedEmail
    }, "POST");
}

function submitPassword() {
    var currentPass = document.getElementById('currentPassword').value;
    var newPass = document.getElementById('newPassword').value;
    post_to_url("/editPass", {
        currentPass: currentPass,
        newPass: newPass
    }, "POST");
}

function cancelEditFullName() {
    document.getElementById('nameSection').style.display = 'block';
    document.getElementById('editNameSection').style.display = 'none';
    console.log("in cancel edit full name");
}

function cancelEditEmail() {
    document.getElementById('emailSection').style.display = 'block';
    document.getElementById('editEmailSection').style.display = 'none';
    console.log("in cancel edit email");
}

function post_to_url(path, params, method) {
    var form = document.createElement("form");
    form.setAttribute("method", method);
    form.setAttribute("action", path);

    for (var key in params) {
        if (params.hasOwnProperty(key)) {
            var hiddenField = document.createElement("input");
            hiddenField.setAttribute("type", "hidden");
            hiddenField.setAttribute("name", key);
            hiddenField.setAttribute("value", params[key]);

            form.appendChild(hiddenField);
        }
    }

    document.body.appendChild(form);
    form.submit();
    form.reset();
}