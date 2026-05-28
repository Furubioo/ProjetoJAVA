# Sistema de Cinema

O projeto em Java busca desenvolver para a disciplina de Programação Orientada a Objetos (POO)
um sistema de cinema utilizando conceitos ministrados em sala de aula (construtores,
métodos getters e setters, classes abstratas, classes de interface, exceção, persistência
epadrões de Projeto).

## Integrantes

- Flávio Daniel
- Maria Clara
- João Pedro

## Estrutura do Repositório

```
cinema-system/               # Versão com frontend
    ├── cinema-backend/   # Spring Boot
    └── cinema-frontend/  # React + Vite
```

---

## cinema-console

### Como executar

1. Clonar o repositório
2. Abrir em uma IDE que reconheça Java (Eclipse / IntelliJ / NetBeans / VS Code)
3. Executar a classe `Main`

### Estrutura do projeto

```
src/                # Versão sem frontend
├── controller/
│   ├── CinemaController.java
│   ├── FilmeController.java
│   └── UsuarioController.java
├── model/
│   ├── strategy/
│   │   ├── EstrategiaPreco.java
│   │   ├── PrecoComum.java
│   │   ├── PrecoCritico.java
│   │   └── PrecoEstudante.java
│   ├── Administrador.java
│   ├── Base.java
│   ├── Bilhete.java
│   ├── Compra.java
│   ├── Critica.java
│   ├── Critico.java
│   ├── CupomPromocional.java
│   ├── Estudante.java
│   ├── Filme.java
│   ├── Funcionario.java
│   ├── GerenciaDeFilmes.java
│   ├── PersistenciaArquivo.java
│   ├── Produto.java
│   ├── Sala.java
│   ├── Sessao.java
│   ├── TipoSala.java
│   ├── Usuario.java
│   └── VendasException.java
├── view/
│   ├── CinemaView.java
│   └── Terminal.java
└── Main.java
```

---

## cinema-web

### Como executar

**Backend**
```bash
cd cinema.backend
./mvnw spring-boot:run
```

**Frontend**
```bash
cd cinema-frontend
npm install
npm run dev
```

> Para resetar o banco de dados, apague o arquivo `cinemadb.mv.db`.

---

*Projeto acadêmico — UNICAP, 2026.*
