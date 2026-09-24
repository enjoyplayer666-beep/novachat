# NovaChat

Самописный чат-плагин для Paper 1.21.1: локальный/глобальный чат, поддержка
& и hex-цветов вместе с MiniMessage, LuckPerms и тег клана SimpleClans через
PlaceholderAPI.

## ВАЖНО: у меня нет доступа в интернет в этой среде

Я не могу скачать Paper API / PlaceholderAPI и собрать .jar прямо здесь —
сеть отключена. Поэтому отдаю тебе **исходники + готовый Maven-проект**,
собрать нужно самому (это займёт 2 минуты, ниже — как именно).

## Как собрать

### Вариант A — если есть Java и Maven на своём ПК
1. Установи JDK 21: https://adoptium.net/ (Temurin 21)
2. Установи Maven: https://maven.apache.org/download.cgi (или `choco install maven` на Windows)
3. В папке проекта выполни:
   ```
   mvn clean package
   ```
4. Готовый файл появится в `target/NovaChat.jar` — закинь его в `plugins/`
   на сервере, перезапусти сервер.

### Вариант B — без установки чего-либо (проще всего)
1. Зайди на https://github.com/, создай новый репозиторий, залей туда эту папку
2. В репозитории добавь файл `.github/workflows/build.yml`:
   ```yaml
   name: build
   on: [push, workflow_dispatch]
   jobs:
     build:
       runs-on: ubuntu-latest
       steps:
         - uses: actions/checkout@v4
         - uses: actions/setup-java@v4
           with:
             distribution: temurin
             java-version: 21
         - run: mvn -B clean package
         - uses: actions/upload-artifact@v4
           with:
             name: NovaChat
             path: target/NovaChat.jar
   ```
3. Закоммить — GitHub сам соберёт jar, скачаешь его из вкладки Actions →
   последний прогон → Artifacts.

### Вариант C — попроси меня собрать позже
Если в будущей сессии у меня появится доступ к интернету (или ты дашь мне
собранный локальный кэш Maven-зависимостей), могу собрать jar прямо тут.

## Настройка

Всё в `src/main/resources/config.yml`:
- `global-symbol` — символ для разового сообщения в глобал (по умолчанию `!`)
- `local-radius` — радиус локального чата в блоках
- `formats.local` / `formats.global` — сами форматы строк, можно смешивать
  `&a`, `&#RRGGBB` и `<gradient>` в одной строке
- `%luckperms_prefix%`, `%luckperms_suffix%`, `%simpleclans_tag%` — уже
  вшиты в формат по умолчанию, ничего доп. настраивать не нужно, если
  PlaceholderAPI + LuckPerms + SimpleClans уже стоят (у тебя стоят)

## Команды

- `/novachat reload` — перезагрузить конфиг (`novachat.admin`)
- `/local` — переключить свой чат на локальный
- `/global` — переключить свой чат на глобальный

## Права

- `novachat.admin` — доступ к reload (по умолчанию у OP)
- `novachat.color` — разрешить игроку свои цвета в сообщениях (по умолчанию выключено)
