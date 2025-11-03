# MilagresDS Mod Audit

Este diretório contém relatórios gerados durante a auditoria de código/recursos para encontrar itens mortos, duplicados ou suspeitos. Cada arquivo será preenchido conforme as etapas forem executadas.

## Resumo do ciclo atual
- Imports redundantes removidos em PlayerAttributes/SpellVisuals e correlatos.
- Renderers/pacotes somente-cliente sem referências internas marcados como `@Deprecated`.
- 19 texturas/modelos (LFS pointers) movidos para `.quarantine/resources/11-03/` para revisão futura.
- Traduções para telas de graça/atributos adicionadas (placeholders em pt_BR) e tooltips órfãos removidos.
- Versões de GeckoLib/MCLib centralizadas no `gradle.properties` e `mods.toml` alinhado com placeholders.
- `.gitattributes` atualizado para garantir tratamento binário de imagens/áudio/JARs.

## Arquivos
- `dead-code.txt`: classes, métodos, campos ou imports potencialmente inutilizados.
- `unused-resources.txt`: recursos que não possuem referência conhecida.
- `duplicates.txt`: arquivos duplicados por hash/nome/semântica.
- `warnings.txt`: entradas suspeitas mantidas por segurança (reflexão, anotação, etc.).
- `actions.todo`: plano das ações aplicadas neste PR.
