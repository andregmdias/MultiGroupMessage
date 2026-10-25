# AGENTS.md - Diretrizes para Agentes e Desenvolvedores 🤖📱

Este documento estabelece as diretrizes de arquitetura, padrões de código, boas práticas Android e políticas de teste para agentes de IA e desenvolvedores que colaboram no projeto **MultiGroupMessage** (referenciando padrões de excelência adotados no **NeuroApp**).

---

## 🏛️ 1. Arquitetura do Projeto (MVVM)

O projeto segue estritamente a arquitetura **MVVM (Model-View-ViewModel)** com fluxo unidirecional de dados (Unidirectional Data Flow - UDF):

```
       +------------------------------------+
       |          UI (Compose Screens)      |
       +-----------------+------------------+
                         |  Events / User Actions
                         v
       +------------------------------------+
       |            ViewModel               |
       +-----------------+------------------+
                         |  Exposes StateFlow
                         v
       +------------------------------------+
       |      Repository (AppRepository)    |
       +-----------------+------------------+
                         |
           +-------------+-------------+
           |                           |
           v                           v
+--------------------+       +-------------------+
|    Room Database   |       |    SharedPreferences / |
|   (AppDatabase)    |       |    AppSettings    |
+--------------------+       +-------------------+
```

### Regras das Camadas:
1. **Model (Camada de Dados)**:
   - Entidades do Room (`ContactEntity`, `GroupEntity`, `CampaignEntity`, `DispatchLogEntity`).
   - DAOs retornando `Flow<T>` para reatividade automática.
   - `AppRepository` abstrai a fonte de dados local do banco de dados e preferências.

2. **ViewModel (Camada de Apresentação)**:
   - Todo estado de UI deve ser exposto como `StateFlow<T>` imutável.
   - Corrotinas devem ser disparadas dentro de `viewModelScope`.
   - O ViewModel **não deve guardar referências de Context de Activity/View** para evitar vazamentos de memória (Memory Leaks). Utilizar `ApplicationContext` via ViewModel Factory quando necessário.

3. **View (Interface Compose)**:
   - Composables devem ser **estritamente reativos** e sem estado interno de regra de negócio (State Hoisting).
   - Utilizar o tema unificado **Flip 7 Card Game** (fundo escuro, cartões elevados e destaques neon de alto contraste).

---

## 🛠️ 2. Boas Práticas de Desenvolvimento Android

1. **Imutabilidade e Reatividade**:
   - Utilize `val` e estruturas de dados imutáveis sempre que possível.
   - Trate listas em estado com `StateFlow` ou `mutableStateListOf` do Compose para garantir recomposição precisa.

2. **Tratamento de Permissões Runtime**:
   - Solicitações de permissões perigosas (como `READ_CONTACTS`) devem ser gerenciadas via `rememberLauncherForActivityResult` e `ActivityResultContracts.RequestPermission()`.

3. **Sanitização de Dados**:
   - Números de telefone e entradas de usuário devem sempre ser limpos e sanitizados via `WhatsAppDispatcher.formatPhoneNumber()` para o formato internacional E.164 antes de qualquer persistência ou disparo.

4. **Navegação Declarativa**:
   - Telas gerenciadas via `NavHost` e rotas fortemente tipadas/definidas na sealed class `Screen`.

---

## 🧪 3. Estratégia de Testes e Política de Cobertura (JaCoCo)

### Política de Cobertura Obrigatória (>= 90%)
- O projeto exige uma cobertura de código de no mínimo **90%** em todas as classes de lógica de negócio (`ViewModel`, `Repository`, `Settings`, `Helpers`).
- O **JaCoCo** está configurado no Gradle (`jacocoTestCoverageVerification`). **A build falhará automaticamente** caso a cobertura seja inferior a 90%.

### Categorias de Teste:
1. **Testes Unitários (JVM - `src/test`)**:
   - Testar todas as funções auxiliares e sanitizadores (`WhatsAppDispatcherTest`).
   - Testar cálculos e transformações de estado do ViewModel (`MainViewModelTest`).
   - Testar preferências e persistência simples (`AppSettingsTest`).
   - Testar operações do Repositório (`AppRepositoryTest`).

2. **Testes de Integração Instrumentados (`src/androidTest`)**:
   - Testar persistência relacional do Room em memória (`AppDatabaseTest`).

---

## 📌 4. Referência do Padrão NeuroApp

Inspirado no projeto **NeuroApp**, este repositório adota:
- **Fail-Fast CI/Build**: Validações automáticas no processo de build que impedem código sem cobertura ou com falhas de compilação de prosseguir.
- **Nomes Significativos e Autoexplicativos**: Métodos, entidades e arquivos devem ser auto-documentados e orientados a seu domínio claro.
- **Isolamento de Responsabilidades**: Separação nítida entre armazenamento de dados local, despacho de Intents externas e renderização gráfica.
