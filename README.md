# event-driven-compiler

Recenhecedor de linguagem SLIP desenvolvida para a disciplina PCS-3866 - Linguagens e Compiladores.

## Lang

Os arquivos [slip.wirth](./res/lang/slip.wirth) e [slip.llk](./res/lang/slip.llk) contém as definições da linguagem em notação de Wirth e em forma LLK, respectivamente.

## Uso

O [executável standalone](./compiler/SLIP-0.1.0.jar) pode ser executado via linha de comando, seguindo as seguintes formas:

### Tokenização léxica a partir de arquivo fonte com escrita de tokens em arquivo json:

```
java -jar SLIP-0.1.0.jar lexicon <arquivo de entrada> -o <arquivo de saída.json> [debug]
```

A flag `debug` faz com que o programa imprima no console a série de eventos léxicos consumidos pelo motor de eventos.

### Reconhecimento sintático a partir de tokens em farquivo json:

```
java -jar SLIP-0.1.0.jar syntax <arquivo de entrada.json> [debug]
```

A flag `debug` faz com que o programa imprima no console os tokens consumidos com sucesso pelo reconhecedor sintático.

### Reconhecimento sintático a partir de arquivo fonte

```
java -jar SLIP-0.1.0.jar full <arquivo de entrada.json> [debug]
```

A flag `debug` faz com que o programa imprima no console as saídas dos motores léxico e sintático.

> Observação: é possível executar o arquivo `.jar` sozinho, via linha de comando, mas não se verá a saída no console.
> É necessário, portanto, ter o ambiente de execução java instalado na máquina, e a possibilidade de referenciá-lo via
> linha de comando (isto é, é necessário adicioná-lo ao PATH do sistema).

## Res

O diretório [res](./res) guarda:

- As definições da linguagem em [lang](./res/lang)
- Arquivos de saída do token léxico em [lexical_tokens](./res/lexical_tokens)
- Arquivos fonte para teste em [program](./res/program)

## License

Copyright © 2021 MComp

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
