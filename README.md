# 🍎 peelweb

**peelweb** é um framework web leve e minimalista feito para Java, ideal para aplicações embarcadas ou com recursos limitados.  
Ele fornece uma camada portátil e expressiva para a criação de aplicações web e servidores HTTP rápidos, sem depender de grandes servidores ou containers.

---

### ✨ Características

- ⚡ **Leve e rápido**: sem dependência de servidores pesados.
- 📦 **Portável**: roda em qualquer dispositivo com Java, de embarcados a desktops.
- 🔌 **Simplicidade na configuração**: inicialização simples via `main()`.
- 🎯 **Enxuto**: inspirado em frameworks minimalistas como Express.js.

---

### 🧠 Acrônimo – PEEL

> **P**ortable • **E**mbedded • **E**xpress • **L**ayer

- **Portable** – Fácil de portar entre diferentes plataformas.
- **Embedded** – Foco em aplicações para sistemas embarcados.
- **Express** – Minimalista e com desempenho ágil.
- **Layer** – Camada de abstração para comunicação web.

---

### 🚀 Inicialização via `public static void main`

```java
public class Application {
    public static void main(String[] args) {
        new PeelApplicationBuilder()
            .contextPath("/api")
            .port(8080)
            .registerController(new MyController())
            .start();
    }
}
