# ROPZ - Rede de Observação de Zonas Quentes

### Integrantes do grupo:
- Diego Costa Silva | RM552648
- Lucas Minozzo Bronzeri | RM553745
- Thais Ribeiro Asfur | RM553870

## Vídeos

- **[Pitch](https://youtu.be/O1Hr3DbmBR4)**
- **[Vídeo teste](https://youtu.be/J8OLpV8tQps)**

## Problema
Com o avanço das mudanças climáticas, eventos de calor extremo
tornaram-se mais frequentes e intensos, impactando diretamente a
saúde pública. Grupos vulneráveis, como crianças, idosos e pessoas
com doenças crônicas, são especialmente suscetíveis a condições
como desidratação, insolação e agravamento de doenças
respiratórias.

Entretanto, a maioria das pessoas não recebe alertas preventivos
eficazes, nem possui informações suficientes sobre como agir
adequadamente para reduzir os riscos associados a temperaturas
elevadas.

## Solução e objetivo
A nossa aplicação java MVC se compromete a entregar ao usuário informações sobre a temperatura de sua região sem pedir muitos dados,
em uma interface fácil de ser entendida, e fácil de fazer leitura/consnumo das informações.

Utiliza-se modelo de IA [Mistral](https://ollama.com/library/mistral) para analisar os dados de temperatura, como:

```java
  temperaturaResponseDTO.temperatura(),
  temperaturaResponseDTO.sensacaoTermica(),
  temperaturaResponseDTO.temperaturaMinima(),
  temperaturaResponseDTO.temperaturaMaxima(),
  temperaturaResponseDTO.umidade(),
  temperaturaResponseDTO.id()
```

E ele promove, em uma linguagem simples, cuidados para previnir problemas de saúde.

## Identidade

![frame-app-design](https://github.com/user-attachments/assets/c8a6ace8-08f0-4f96-8cc1-b16a5af8345a)

## Modelo banco de dados







