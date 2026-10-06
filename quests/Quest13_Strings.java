// ============================================================
// QUEST 13 · ИНЖЕНЕР · Строки в бою: методы + live-coding классика
// Уровни ROADMAP: LVL 21Б, 21В, 21Г, 21Д, 36Б
//
//   ./q 13
//
// Секция A — методы String (то, что спрашивают "напишите прямо сейчас").
// Секция B — классика live-coding: reverse, palindrome, anagram...
// Договоримся: работаем с латиницей в нижнем регистре, если не сказано иначе.
// Максимум: 285 XP.
// ============================================================

import java.util.Arrays;

public class Quest13_Strings {

    static int xp = 0;

    static void check(boolean condition, String name, int reward) {
        if (condition) {
            xp += reward;
            System.out.println("✅ " + name + " (+" + reward + " XP)");
        } else {
            System.out.println("❌ " + name + " — неверный результат");
        }
    }

    static void todo(String name) {
        System.out.println("⬜ " + name + " — ещё не сделано");
    }

    public static void main(String[] args) {
        System.out.println("=== QUEST 13 · СТРОКИ ===");

        try {
            check(mid("Союз-12").equals("юз") && tail("Союз", 2).equals("юз"), "A1 · substring: середина и хвост", 15);
        } catch (UnsupportedOperationException e) {
            todo("A1 · substring: середина и хвост");
        }
        try {
            check(indexOf2nd("абракадабра", "а") == 3, "A2 · indexOf со стартом", 15);
        } catch (UnsupportedOperationException e) {
            todo("A2 · indexOf со стартом");
        }
        try {
            check(mask("RK-123-45").equals("RK-***-45"), "A3 · replace цифрами звёздочки", 15);
        } catch (UnsupportedOperationException e) {
            todo("A3 · replace цифрами звёздочки");
        }
        try {
            check(words(" полетели  на   Марс ").length == 3, "A4 · split по пробелам", 20);
        } catch (UnsupportedOperationException e) {
            todo("A4 · split по пробелам");
        }
        try {
            check(normalize("  SoYuz-9 ").equals("soyuz-9"), "A5 · strip + lower", 15);
        } catch (UnsupportedOperationException e) {
            todo("A5 · strip + lower");
        }
        try {
            check(isRocketId("RK-123") && !isRocketId("rk-123") && !isRocketId("RK-12"),
                    "A6 · startsWith/endsWith/contains + длина", 20);
        } catch (UnsupportedOperationException e) {
            todo("A6 · startsWith/endsWith/contains + длина");
        }
        try {
            check(reverse("Союз").equals("зюоС"), "B1 · разворот", 20);
        } catch (UnsupportedOperationException e) {
            todo("B1 · разворот");
        }
        try {
            check(isPalindrome("шалаш") && !isPalindrome("ракета"), "B2 · палиндром", 20);
        } catch (UnsupportedOperationException e) {
            todo("B2 · палиндром");
        }
        try {
            check(countChar("абракадабра", 'а') == 5, "B3 · частота символа", 15);
        } catch (UnsupportedOperationException e) {
            todo("B3 · частота символа");
        }
        try {
            check(firstUnique("абракадабра") == 1 && firstUnique("аа") == -1,
                    "B4 · первый уникальный (индекс!)", 25);
        } catch (UnsupportedOperationException e) {
            todo("B4 · первый уникальный (индекс!)");
        }
        try {
            check(areAnagrams("listen", "silent") && !areAnagrams("союз", "ракета"),
                    "B5 · анаграммы", 25);
        } catch (UnsupportedOperationException e) {
            todo("B5 · анаграммы");
        }
        try {
            check(wordCount("полетели на Марс") == 3, "B6 · число слов", 15);
        } catch (UnsupportedOperationException e) {
            todo("B6 · число слов");
        }
        try {
            check(longestWord("полетели на Марс").equals("полетели"), "B7 · самое длинное слово", 20);
        } catch (UnsupportedOperationException e) {
            todo("B7 · самое длинное слово");
        }
        try {
            check(removeDuplicates("абракадабра").equals("абркд"), "B8 · убрать дубликаты", 25);
        } catch (UnsupportedOperationException e) {
            todo("B8 · убрать дубликаты");
        }
        try {
            check(joinNonEmpty("-", "а", "", "б").equals("а-б"), "B9 · String.join без пустых", 20);
        } catch (UnsupportedOperationException e) {
            todo("B9 · String.join без пустых");
        }

        try {
            check(ctConcat() && !rtConcat() && interned(), "B10 · пул: константы да, runtime нет, intern чинит", 60);
        } catch (UnsupportedOperationException e) {
            todo("B10 · пул: конкатенация и intern");
        }

        System.out.println("------------------------");
        System.out.println("ИТОГО XP: " + xp + " / 345");
    }

    // B10 · Три факта про пул в одном задании:
    // (1) конкатенация ЛИТЕРАЛОВ считается компилятором → тот же объект пула;
    // (2) конкатенация с new String считается в РАНТАЙМЕ → новый объект, == врёт;
    // (3) intern() кладёт строку в пул вручную.
    // Сначала ПРЕДСКАЖИ все три ответа вслух, потом напиши код!
    static boolean ctConcat() {
        // TODO B10: ✍️ String a = "Со" + "юз"; return a == "Союз";
        throw new UnsupportedOperationException("B10a not implemented");
    }

    static boolean rtConcat() {
        // TODO B10: ✍️ String part = new String("юз"); String s = "Со" + part; return s == "Союз";
        throw new UnsupportedOperationException("B10b not implemented");
    }

    static boolean interned() {
        // TODO B10: ✍️ String s = new String("Союз").intern(); return s == "Союз";
        throw new UnsupportedOperationException("B10c not implemented");
    }

    // A1 · mid: символы 1..3 ("Союз-12" → "юз"); tail: последние n символов.
    static String mid(String s) {
        // TODO A1: ✍️ return s.substring(1, 3);
        throw new UnsupportedOperationException("A1a not implemented");
    }

    static String tail(String s, int n) {
        // TODO A1: ✍️ return s.substring(s.length() - n);
        throw new UnsupportedOperationException("A1b not implemented");
    }

    // A2 · Индекс ВТОРОГО вхождения подстроки. Подсказка: indexOf(str, fromIndex).
    static int indexOf2nd(String s, String sub) {
        // TODO A2: ✍️ int first = s.indexOf(sub); return s.indexOf(sub, first + 1);
        throw new UnsupportedOperationException("A2 not implemented");
    }

    // A3 · Замени все цифры на '*'. Подсказка: replaceAll("\\d", "*") — это уже почти regex!
    static String mask(String s) {
        // TODO A3: ✍️ нужен replaceAll с шаблоном «цифра». Какой класс символов её означает? (Подсказка LVL 21Е: backslash-d)
        throw new UnsupportedOperationException("A3 not implemented");
    }

    // A4 · Разбей по пробелам, пустые куски выкинь. Подсказка: trim + split("\\s+")!
    static String[] words(String s) {
        // TODO A4: ✍️ убери края, разбей по «пучкам пробелов» (шаблон: backslash-s-плюс). А что даст пустая строка? Проверь отдельно!
        throw new UnsupportedOperationException("A4 not implemented");
    }

    // A5 · Нормализация идентификатора: убрать края + нижний регистр.
    static String normalize(String s) {
        // TODO A5: ✍️ скомпонуй два вызова в цепочку (убрать края, потом регистр). Порядок важен? Подумай!
        throw new UnsupportedOperationException("A5 not implemented");
    }

    // A6 · ID ракеты: начинается с "RK-", длина 6. Собери проверку из трёх условий.
    static boolean isRocketId(String s) {
        // TODO A6: ✍️ собери проверку из startsWith + длины. endsWith/contains потрогай в отладчике сам — Evaluate Expression!
        throw new UnsupportedOperationException("A6 not implemented");
    }

    // B1 · Разворот через StringBuilder (так быстрее, чем + в цикле!).
    static String reverse(String s) {
        // TODO B1: ✍️ у StringBuilder есть метод для этого (начинается на r...). Найди через автодополнение IDE!
        throw new UnsupportedOperationException("B1 not implemented");
    }

    // B2 · Палиндром = равен своему развороту. Используй B1!
    // ДЛЯ СМЕЛЫХ: версия без учёта регистра и пробелов ("А роза упала на лапу Азора" — да!). Отдельным методом!
    static boolean isPalindrome(String s) {
        // TODO B2: ✍️ вырази через свою B1: палиндром равен своему развороту. Чем сравнить? (вспомни LVL-21!)
        throw new UnsupportedOperationException("B2 not implemented");
    }

    // B3 · Сколько раз символ встречается. Подсказка: charAt в цикле.
    static int countChar(String s, char c) {
        // TODO B3: ✍️ пройдись charAt'ом от 0 до length и посчитай совпадения с c
        throw new UnsupportedOperationException("B3 not implemented");
    }

    // B4 · Индекс первого НЕповторяющегося символа или -1.
    // Подсказка: для каждой позиции посчитай countChar — O(n²), но честно и понятно.
    static int firstUnique(String s) {
        // TODO B4: ✍️ для каждой позиции посчитай частоту СВОИМ countChar; первая с частотой 1 — ответ. Какая сложность? (честно!)
        throw new UnsupportedOperationException("B4 not implemented");
    }

    // B5 · Анаграммы: одинаковые буквы в разном порядке.
    // Подсказка: toCharArray + Arrays.sort + Arrays.equals. Нужен import java.util.Arrays!
    static boolean areAnagrams(String a, String b) {
        // TODO B5: ✍️ приведи оба слова к каноническому виду (массив символов + сортировка) и сравни канонические формы
        throw new UnsupportedOperationException("B5 not implemented");
    }

    // B6 · Число слов: используй words() из A4!
    static int wordCount(String s) {
        // TODO B6: ✍️ используй свои words() из A4. А что вернёт пустая строка? Запусти, объясни, это ловушка собеседований!
        throw new UnsupportedOperationException("B6 not implemented");
    }

    // B7 · Самое длинное слово (первое при равенстве).
    static String longestWord(String s) {
        // TODO B7: ✍️ иди по words(), держи лучшее по length; при равенстве НЕ обновляй (нужно первое!)
        throw new UnsupportedOperationException("B7 not implemented");
    }

    // B8 · Убери дубликаты символов, порядок сохрани ("абракадабра" → "абркд").
    // Подсказка: StringBuilder + indexOf(c) == -1 значит "ещё не было".
    static String removeDuplicates(String s) {
        // TODO B8: ✍️ убери дубликаты символов, порядок сохрани. Чистый лист: ни API, ни шагов не даём — только проверка выше!
        throw new UnsupportedOperationException("B8 not implemented");
    }

    // B9 · Склей через разделитель, пропуская пустые/null. Подсказка: собери список, потом String.join.
    // Понадобится import java.util.ArrayList; (компилятор подскажет, добавь сам!).
    static String joinNonEmpty(String sep, String... parts) {
        // TODO B9: ✍️ отфильтруй мусор в новый ArrayList, склей String.join. Понадобится import ArrayList!
        // (ArrayList — список, который умеет расти; подробно разберём на LVL 23, пока просто поверь и используй.)
        throw new UnsupportedOperationException("B9 not implemented");
    }
}
