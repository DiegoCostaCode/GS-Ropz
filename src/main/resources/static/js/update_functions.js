// document.addEventListener("DOMContentLoaded", () => {
//     console.log("DOM carregado, iniciando processo");
//     const tempSpan = document.getElementById("temp-info");
//     const temperaturaId = tempSpan?.dataset?.tempId;
//     console.log("Temperatura ID obtida:", temperaturaId);
//
//     if (temperaturaId) {
//         verificarRelatorio(temperaturaId);
//     } else {
//         console.log("Nenhum ID de relatório encontrado, não iniciando verificação");
//     }
// });
//
// function verificarRelatorio(idTemperatura) {
//     let tentativas = 0;
//     const maxTentativas = 200;
//
//     const intervalo = setInterval(async () => {
//         if (tentativas >= maxTentativas) {
//             console.warn("Número máximo de tentativas alcançado, parando o processo");
//             clearInterval(intervalo);
//             return;
//         }
//
//         try {
//             const response = await fetch(`relatorio/api/temperatura/${idTemperatura}`);
//
//             if (!response.ok) {
//                 console.log("Resposta não OK, ignorando este ciclo");
//                 return;
//             }
//
//             const data = await response.json();
//
//             if (data.risco && data.mensagem) {
//
//                 document.getElementById("risco-texto").textContent = data.risco;
//                 document.getElementById("mensagem-texto").textContent = data.mensagem;
//
//                 const section = document.querySelector(".section-status-actual-weather");
//                 const loading = document.querySelector(".section-status-loading");
//
//                 if (section && loading) {
//                     section.style.display = "flex";
//                     loading.style.display = "none";
//                 } else {
//                     console.log("Seção .section-status-actual-weather não encontrada");
//                 }
//
//                 clearInterval(intervalo);
//             } else {
//                 console.warn("Risco ou mensagem não encontrados no dado retornado");
//             }
//         } catch (error) {
//             console.error("Erro ao buscar relatório:", error);
//         }
//
//         tentativas++;
//     }, 10000);
// }
//
//
//
//
//
//
