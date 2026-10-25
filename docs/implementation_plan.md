# Plano de Implementação Geral - MultiGroupMessage

Este documento centraliza os planos de implementação do projeto **MultiGroupMessage**, abrangendo o desenvolvimento do aplicativo Android em Kotlin com Jetpack Compose, suporte a WhatsApp & WhatsApp Business, diretrizes de arquitetura (`AGENTS.md`), relatórios de cobertura do JaCoCo (>= 90%) e documentação do projeto.

---

## 🏗️ 1. Arquitetura e Estrutura Inicial do App
- **Objetivo**: Aplicativo Android nativo para envio simultâneo de mensagens para grupos e contatos selecionados.
- **Integração WhatsApp / WhatsApp Business**:
  - Uso de Intents nativos de Deep Link (`https://api.whatsapp.com/send?phone=...&text=...`).
  - Suporte e alternância dinâmica entre WhatsApp (`com.whatsapp`) e WhatsApp Business (`com.whatsapp.w4b`).
  - Permissão `READ_CONTACTS` para importação da agenda do aparelho.
- **Fila de Disparo Inteligente (Campaign Dispatcher)**:
  - Fila com progresso em tempo real, cartão de destaque do contato atual, botão "Enviar Agora" e "Pular".
- **Métricas e Analytics**:
  - Registro de 1 Campanha por lote disparado.
  - Cálculo da relação *Mensagens x Pessoas Atendidas* e estatísticas de alcance único.
- **Design Flip 7 Card Game**:
  - Paleta de alto contraste com fundo escuro (`#0F141A`), superfícies de cartão (`#1A222B`), Verde Neon (`#00E676`), Ciano Elétrico (`#00E5FF`), Dourado Cyber (`#FFD600`) e Vermelho Vívido (`#FF3D00`).

---

## 📑 2. Documentação e FAQ no App
- **`README.md`**: Guia completo de uso, passo a passo para configurar o WhatsApp Business, instruções de vinculação com o **WhatsApp Web** e explicação detalhada sobre a **gratuidade dos envios nativos** vs APIs pagas da Meta.
- **`FaqScreen.kt`**: Tela de Perguntas Frequentes interativa integrada no app com respostas expansíveis para dúvidas sobre WhatsApp Business, cobrança, WhatsApp Web e boas práticas.

---

## 🧪 3. Diretrizes de Desenvolvimento e JaCoCo (90% Cobertura)
- **`AGENTS.md`**: Instruções para agentes e desenvolvedores com diretrizes de MVVM, retenção de estado, desacoplamento do Context e convenções do **NeuroApp**.
- **Configuração do JaCoCo**:
  - Plugin `jacoco` aplicado com regra de verificação estrita `minimum = 0.90` (90% de cobertura).
  - A execução de `./gradlew check` ou `./gradlew testDebugUnitTest` quebra o processo de build se a cobertura for inferior a 90%.
- **Suíte de Testes Unitários (17 Testes com 100% de Aprovação)**:
  - `WhatsAppDispatcherTest`, `AppAnalyticsTest`, `AppSettingsTest`, `AppRepositoryTest` e `MainViewModelTest`.
- **Testes de Integração Instrumentados**:
  - `AppDatabaseTest` para Room Database.
