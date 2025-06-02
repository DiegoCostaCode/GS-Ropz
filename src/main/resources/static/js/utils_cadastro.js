function formatarTelefone(input) {
    let telefone = input.value.replace(/\D/g, '');
    telefone = telefone.replace(/^(\d{2})(\d)/g, '($1) $2');
    telefone = telefone.replace(/(\d{4,5})(\d{4})$/, '$1-$2');
    input.value = telefone;
}

function formatarCep(input) {
    let cep = input.value.replace(/\D/g, '');
    cep = cep.replace(/^(\d{5})(\d)/, '$1-$2');
    input.value = cep;
}

