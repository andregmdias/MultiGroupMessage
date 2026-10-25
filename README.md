# MultiGroupMessage 📲💬

**MultiGroupMessage** é um aplicativo Android nativo desenvolvido em Kotlin e Jetpack Compose para gerenciamento de grupos de contatos, envio de mensagens simultâneas em lote via **API Oficial do WhatsApp Business (Meta Cloud API)**, acompanhamento de métricas de alcance e relação *Mensagens x Pessoas Atendidas*.

O aplicativo possui uma interface moderna de alto contraste inspirada na estética do jogo **Flip 7 Card Game** (cartões escuros, contornos iluminados e destaques em verde neon, ciano elétrico e dourado).

---

## 📑 Sumário
- [Recursos Principais](#-recursos-principais)
- [Funcionamento da API da Meta: Grupos de Conversa e Janela de 24h](#-funcionamento-da-api-da-meta-grupos-de-conversa-e-janela-de-24h)
- [Como Configurar e Testar a API Oficial do WhatsApp Business](#-como-configurar-e-testar-a-api-oficial-do-whatsapp-business)
- [Como Obtemos Credenciais no Meta for Developers](#-como-obter-as-credenciais-no-meta-for-developers)
- [Como Configurar o WhatsApp Web](#-como-configurar-o-whatsapp-web)
- [Como Funciona a Cobrança e Custos](#-como-funciona-a-cobrança-e-custos)
- [Boas Práticas e Prevenção de Bloqueios](#-boas-práticas-e-prevenção-de-bloqueios)
- [Arquitetura e Tecnologias](#-arquitetura-e-tecnologias)

---

## 🚀 Recursos Principais

1. **Disparo Automático em Lote (Meta Cloud API)**:
   - Dispara todas as mensagens em lote diretamente pelo aplicativo no segundo plano via chamadas autenticadas na Graph API da Meta, sem a necessidade de redirecionar para o WhatsApp ou realizar toques manuais por mensagem.

2. **Acesso Exclusivo à API Oficial da Meta**:
   - Foco exclusivo na API Oficial da Meta para evitar o risco de bloqueios ou banimentos causados por automação abusiva em contas pessoais do WhatsApp.
   - Sanitização automática de números de telefone para o formato internacional E.164 (inclusão/ajuste de DDI e DDD).

3. **Gerenciador de Grupos e Contatos**:
   - Importação direta de contatos da agenda do telefone (permissão `READ_CONTACTS`).
   - Criação, edição e exclusão de grupos de contatos com cores e tags personalizadas.

4. **Contador de Campanhas e Painel de Métricas**:
   - Cada disparo em massa para N pessoas é registrado como **1 Campanha**.
   - Painel de Analytics completo calculando a relação **Mensagens x Pessoas Atendidas**, alcance único e média de contatos por envio.

---

## 📌 Funcionamento da API da Meta: Grupos de Conversa e Janela de 24h

> [!NOTE]
> **Por que não é possível enviar para os "Grupos de Conversa" do app WhatsApp via API Oficial?**

1. **Grupos de Conversa Proibidos na API Oficial**:
   - A **WhatsApp Business Cloud API** foi desenhada exclusivamente para comunicação **B2C (Empresa para Cliente Individual)**.
   - A Meta **não disponibiliza na API Oficial** suporte para criar, listar ou postar em "Grupos de Conversa" (group chats) do WhatsApp. A funcionalidade é explicitamente restrita para combater spam.

2. **Como o MultiGroupMessage Resolve**:
   - O aplicativo gerencia suas audiências e grupos de contatos (**Grupos do App**).
   - Ao disparar para um grupo de 50 pessoas, o aplicativo realiza 50 requisições HTTP individuais em lote para a API da Meta. Cada destinatário recebe a mensagem como uma **conversa privada e exclusiva** da sua empresa.

3. **Janela de Atendimento de 24 Horas**:
   - **Dentro de 24h** (quando o cliente te enviou uma mensagem recentemente): É possível enviar qualquer texto livre diretamente.
   - **Fora de 24h** (primeiro contato ou após 24h): A Meta exige o uso de **Modelos de Mensagem (Templates)** aprovados no portal Meta Business Suite.

---

## 🧪 Como Configurar e Testar a API Oficial do WhatsApp Business

### 1. Criar Conta e Obter Credenciais na Meta
1. Acesse [developers.facebook.com](https://developers.facebook.com) e crie uma conta de desenvolvedor gratuita.
2. Crie um aplicativo do tipo **Empresa (Business)** e adicione o produto **WhatsApp**.
3. Associe o aplicativo a uma **Conta do Meta Business** (*Meta Business Account* / Portfólio de Negócios) quando solicitado.
4. Na página **Configuração da API (API Setup)** (menu lateral esquerdo), copie:
   - **Token de Acesso Temporário** (`Access Token`) no topo da página.
   - **ID do Número de Telefone** (`Phone Number ID`) na parte inferior, abaixo do número de teste.

### 2. Autorizar seu Celular para Testes
1. Na mesma página da Meta, no campo **Para (To)**, selecione *Gerenciar lista de números de telefone*.
2. Cadastre o seu número de celular pessoal e digite o código de verificação recebido por SMS/WhatsApp.

### 3. Configurar e Testar no MultiGroupMessage
1. Abra o **MultiGroupMessage** e acesse a aba **Ajustes (Configurações)**.
2. Cole o **Phone Number ID** e o **Token de Acesso**.
3. No campo **Testar Conexão**, digite seu número de telefone e clique em **TESTAR ENVIO**.
4. O aplicativo enviará uma mensagem de teste instantânea para o seu WhatsApp e confirmará com o ID da mensagem (`wamid`).

---

## 🔑 Como Obter as Credenciais no Meta for Developers

No aplicativo, na tela de **Ajustes (Configurações)**, você encontrará um ícone de informação (**ℹ️**) no canto superior direito do cartão de credenciais. Ao clicar nele, um modal exibirá o guia:

1. **Associação ao Portfólio de Negócios**:
   - Crie o App e associe-o obrigatoriamente a uma **Conta Empresarial (Meta Business Account)**.

2. **Access Token**:
   - Localizado em: `developers.facebook.com` -> *Meus Aplicativos* -> *Seu App* -> *WhatsApp* -> *Configuração da API (API Setup)*.
   - Na caixa de destaque no topo, copie o **Token de acesso temporário** (válido por 24h para testes).

3. **Phone Number ID**:
   - Na mesma tela *Configuração da API*, role a página até a seção de "Enviar e receber mensagens".
   - Copie o **ID do número de telefone** logo abaixo do número de telefone de teste gerado pela Meta.

---

## 💻 Como Configurar o WhatsApp Web

Você pode acompanhar e gerenciar todas as mensagens disparadas pelo aplicativo direto no computador utilizando o **WhatsApp Web**:

1. Abra o **WhatsApp** ou **WhatsApp Business** no seu celular.
2. Toque no menu de **três pontos** (canto superior direito) e selecione **Dispositivos Conectados**.
3. Toque em **Conectar um dispositivo**.
4. No navegador do seu computador, acesse [web.whatsapp.com](https://web.whatsapp.com).
5. Aponte a câmera do celular para o **QR Code** exibido na tela do computador.
6. Todas as mensagens enviadas aparecerão no seu WhatsApp Web em tempo real!

---

## 💰 Como Funciona a Cobrança e Custos

- A Meta concede **1.000 conversas gratuitas por mês** para contas de teste/desenvolvimento.
- Para volumes maiores em produção comercial, a Meta cobra uma tarifa por conversa iniciada dependendo do país de destino.

---

## 🛡️ Boas Práticas e Prevenção de Bloqueios

1. **Envie Apenas com Consentimento**: Envie mensagens para clientes e pessoas que autorizaram o contato (*opt-in*).
2. **Utilize Nomes nos Grupos**: Organize contatos em grupos segmentados no app.
3. **Respeite o Ritmo de Envio**: Mantenha mensagens relevantes e evite spam massivo para contatos desconhecidos.

---

## 🛠️ Arquitetura e Tecnologias

- **Linguagem**: Kotlin 2.2+
- **Interface Gráfica**: Jetpack Compose com Material 3 (Tema Customizado *Flip 7 Card Game*)
- **Banco de Dados Local**: Room Database 2.8+ com KSP (Kotlin Symbol Processing)
- **Integração WhatsApp**: WhatsApp Business Meta Cloud API (`graph.facebook.com/v22.0`) e `ContactsContract`
- **Cobertura de Código**: JaCoCo configurado com verificação estrita de **>= 90%**
