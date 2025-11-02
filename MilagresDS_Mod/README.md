# MilagresDS Mod

## Dev Quickstart (1.20.1)
- Instale o **JDK 17**.
- Execute `./gradlew --refresh-dependencies clean`.
- Execute `./gradlew genIntelliJRuns`.
- Execute `./gradlew runClient`.

GeckoLib 4.7.x é resolvido automaticamente via Maven (Cloudsmith/Modrinth).

## Limpeza antes de rodar
```
gradlew --stop
gradlew clean
# Apagar manualmente:
# %USERPROFILE%\.gradle\caches\forge_gradle
# %USERPROFILE%\.gradle\caches\modules-2\files-2.1\software.bernie.geckolib
# Excluir a pasta 'run' do projeto
```
