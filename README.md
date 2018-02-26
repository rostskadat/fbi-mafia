# fbi-mafia

## Use cases
• Add (boss, subordinates, siblings)
• ‎remove (boss, subordinate, siblings)
• ‎count shallow (subordinates)
• ‎count deep (subordinates & their subordinates)

## Structures:
• Tree
• ‎tree + lookup map (Id, node)
• ‎relation map (boss id, subordinate id)
• ‎full path map (id1, id2... Idn)

## Complexity

|             | Add    | Remove | Count shallow | Count deep |
|------------:|:------:|:------:|:-------------:|:----------:|
| Tree        | medium | medium | medium        | complex    |
| Tree+LM     | simple | simple | simple        | medium     |
| Relation map| medium | simple | simple        | complex    |
| Path map    | medium | medium | simple        | simple     |

