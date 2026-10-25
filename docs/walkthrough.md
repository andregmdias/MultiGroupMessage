# MultiGroupMessage - Walkthrough & Evidências

O aplicativo **MultiGroupMessage** conta com a integração da **API Oficial do WhatsApp Business (Meta Cloud API)** para disparo **automático direto em lote no segundo plano**, recurso de **Teste de Envio de Mensagem**, o guia de arquitetura **`AGENTS.md`**, a configuração do **JaCoCo com verificação estrita de 90% de cobertura mínima** e a suíte de testes unitários ampliadas com **21 testes e 100% de aprovação**.

Toda a documentação do aplicativo está atualizada para as regras vigentes do Meta for Developers (Vinculação à Meta Business Account e Tela de Configuração da API - API Setup).

---

## 📑 Funcionalidades e Telas Implementadas

1. **`HomeScreen` (Início)**:
   - Cartão CTA principal para criação de mensagens em lote.
   - Painel de métricas rápidas (*Campanhas*, *Impacto* e *Relação Média Mensagens x Pessoas*).
   - Acesso rápido aos grupos cadastrados.

2. **`SendMessageScreen` (Sessão de Disparo Automático Exclusivo pela API Oficial)**:
   - Foco total e exclusivo na Meta Cloud API para garantir a segurança da conta pessoal do usuário contra banimentos por spam.
   - Seleção por grupos ou contatos individuais.
   - Botão **"DISPARAR TUDO AUTOMATICAMENTE"**: Envia todas as mensagens sequencialmente em segundo plano via HTTP POSTs autenticados na Graph API da Meta sem abrir telas ou exigir cliques manuais.
   - Modal da Fila de Disparo com progresso em tempo real e atualização de status automática.

3. **`SettingsScreen` (Ajustes & Credenciais Meta)**:
   - Formulário para cadastro do **Phone Number ID** e **Access Token** do Meta for Developers.
   - Ícone de Informação (**ℹ️**) que abre um tutorial modal mostrando exatamente como gerar os dados no painel da Meta (incluindo a necessidade de vincular o App a uma Conta do Meta Business / Portfólio de Negócios).
   - Botão de **"TESTAR CONEXÃO / ENVIAR MENSAGEM DE TESTE"**: Permite enviar uma mensagem de teste para o próprio celular e validar as credenciais instantaneamente.

4. **`FaqScreen` (Perguntas Frequentes & Ajuda)**:
   - Guia expansível cobrindo o novo passo a passo da Meta Cloud API, como realizar o teste de envio gratuito e como gerenciar conversas usando o WhatsApp Web.

5. **`GroupsScreen` & `AnalyticsScreen`**:
   - Gestão de grupos/contatos e painel de métricas com a relação *Mensagens x Pessoas Atendidas*.

---

## 🧪 Suíte de Testes e Cobertura JaCoCo

### Testes Unitários (`app:testDebugUnitTest`)
- **`WhatsAppCloudApiClientTest.kt`**: Validação da construção de chamadas para a Meta Graph API e tratamento de erros de credenciais.
- **`WhatsAppDispatcherTest.kt`**: Sanitização e formatação E.164 de números de telefone.
- **`AppAnalyticsTest.kt`**: Cálculos de estatísticas de campanha e médias.
- **`AppSettingsTest.kt`**: Gerenciamento das credenciais da Cloud API e modo de disparo.
- **`AppRepositoryTest.kt`**: Operações do repositório local.
- **`MainViewModelTest.kt`**: Lógica da ViewModel, disparo individual, teste de mensagem e disparo automático em lote.

> **Resultado**: `21 passed, 0 skipped, 0 failed` em `./gradlew app:testDebugUnitTest`.

### Relatório de Cobertura JaCoCo
- Tarefas `jacocoTestReport` e `jacocoTestCoverageVerification` ativas.
- Regra de verificação com limite mínimo de **90% (0.90)** aprovada com sucesso.

---

## 🏗️ Status de Compilação
- `./gradlew app:testDebugUnitTest`: **21 passed, 0 failed**.
- `./gradlew app:jacocoTestCoverageVerification`: **PASSED (>= 90% Cobertura)**.
- `./gradlew app:assembleDebug`: **BUILD SUCCESSFUL**.
