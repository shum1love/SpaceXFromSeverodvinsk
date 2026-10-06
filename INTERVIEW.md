# INTERVIEW.md — Top 30 вопросов + Top 10 live-coding

> Банк финальной проверки. Правило: сначала ответь/реши САМ (вслух, с таймером,
> без подглядываний), потом сверься с LEARNING/квестом в скобках.
> Это дополнение к Gauntlet (LVL 55–66), а не замена: Gauntlet — бои по времени,
> здесь — прицельный добор.

## Как пользоваться

1. Тяни вопросы случайно (кубик, `shuf`, бумажки) — как на интервью, вразнобой.
2. На каждый вопрос: ответ → пример кода/контрпример → follow-up себе («а почему?»).
3. Не ответил — вернись по ссылке, добей, повтори через 2–3 дня (🔁).

## Top 30 Java Core questions

### База и память
1. Чем примитив отличается от ссылки? Где живут локальные переменные? ([LEARNING](LEARNING.md) — примитивы vs ссылки; LVL 9Ж)
2. Что значит «Java всегда pass-by-value»? Докажи на методе, меняющем поле, но не ссылку. (Quest08, задачи 6–8)
3. Что будет при `Integer.MAX_VALUE + 1` и почему? А при `7/2`? (Quest07, LVL 6В)
4. Чем `==` отличается от `equals` для строк и объектов? (Quest03, LVL 21)
5. Где живут объекты, что такое достижимость и корни GC? (Quest09, LVL 11В–11Г)
6. Почему `System.gc()` ничего не гарантирует? Бывают ли утечки в Java? (Quest29-8, LVL 62)

### static / final / ООП
7. Что такое `static`? Почему у static-метода нет `this`? Порядок инициализации? (Quest01, LVL 11)
8. Что фиксирует `final` у ссылки? Чем `final List` отличается от immutable-списка? (Quest11, LVL 15Б)
9. Overload vs override: чем биндится каждое? (Quest10, задачи 3–4)
10. interface vs abstract class: когда что + пример из Space Company. (LVL 19Б)
11. Наследуются ли конструкторы? Что делает `super()` первой строкой? (Quest10/Quest29-10, LVL 16)

### String / equals / коллекции
12. Почему String immutable и при чём тут пул? `new String("a") == "a"`? (Quest13 B10, Quest29-6, LVL 21/58)
13. Контракт `equals/hashCode` дословно. Что сломается при нарушении? (Quest03/Quest11/Quest29-9)
14. Могут ли разные объекты иметь равный hashCode? А равные объекты — разный? (Quest29-4)
15. ArrayList vs LinkedList: устройство, Big O, ловушка про «вставку O(1)». (Quest15, LVL 23Г/56)
16. Как работает HashMap: put, bucket, коллизия, `equals`, resize, treeification? (Quest16, LVL 25В–25Д, 57)
17. Можно ли null-ключ? Что будет с изменяемым ключом? Thread-safe ли HashMap? (Quest16, задачи 2/4)
18. `Comparable` vs `Comparator`; как отсортировать по двум полям? (Quest17, LVL 27Б)
19. Зачем generics? Почему `List<Integer>` не `List<Number>`? PECS? (Quest27, LVL 26В/59)
20. Что стирание запрещает (`new T`, `instanceof T`) и как обходят? (Quest27, задача 8)

### Исключения / Stream / Optional
21. Checked vs unchecked + примеры обоих из Space Company. (Quest20, LVL 60)
22. Что вернёт `try{return 1;} finally{return 2;}`? А если в finally только `x=2`? (Quest20, задачи 4–5)
23. Intermediate vs terminal операции; почему стрим одноразовый? (Quest18 D2, LVL 30Е)
24. `map` vs `flatMap`; `orElse` vs `orElseGet` — в чём разница цены? (Quest18 A3, Quest19-3/4)
25. Где Optional НЕ нужен? (LVL 32Г: поля, параметры, коллекции)

### JVM / потоки / инструменты
26. Stack vs Heap; StackOverflowError vs OutOfMemoryError — откуда каждый? (Quest09-9, LVL 62)
27. Race condition + пример с балансом; volatile vs synchronized vs AtomicInteger — что чинит каждый? (Quest24, LVL 49В)
28. Почему `volatile` не делает `counter++` атомарным? (Quest29-3)
29. Что такое deadlock и 3 способа профилактики? (Quest24, LVL 50Б)
30. JUnit: lifecycle, AAA, параметризация; Maven: фазы, скоупы, dependency vs plugin. (LVL 42Е, 46В)

## Top 10 Live Coding

Решай вслух, с примером ДО кода и прогоном ПОСЛЕ. Время — 10–25 минут на задачу.

1. **Разворот строки + палиндром** (Quest13 B1–B2; усложнение: без учёта регистра/пробелов).
2. **Частота символов + первый уникальный** (Quest13 B3–B4; спроси себя про O(n) через карту).
3. **Анаграммы** (Quest13 B5; потом — группировка анаграмм, Quest26-4).
4. **Второй максимум / max-min за проход** (Quest07 B3/B6; краевые: пустой, дубли).
5. **Two-sum за один проход** (Quest16-6; объясни сложность).
6. **Частота слов + топ-N** (Quest16-1 → Quest26-2; HashMap, потом стримом).
7. **Группировка/подсчёт стримом** (Quest18 BOSS, Quest26-8; `groupingBy` + downstream).
8. **Убрать дубликаты с порядком** (Quest13 B8; потом — через Stream `distinct`).
9. **Optional-цепочка** (`find → map → orElse`; Quest19-5/6; объясни каждый шаг).
10. **Мини-аналитика логов** (Quest16-10 / Quest26-8: распарсить, посчитать, отформатировать отчёт).

## Связь с Gauntlet

- LVL 55–60: теория из Top 30 (тяни по 2 случайных + follow-up!).
- LVL 61: задачи 1–7 из Top 10 на время.
- LVL 62–63: вопросы 5–6, 26–29.
- LVL 64: баги + Quest29-8 (утечка) как разбор.
- LVL 66: бери вопросы и задачи прямо отсюда в раунды 1–2.
