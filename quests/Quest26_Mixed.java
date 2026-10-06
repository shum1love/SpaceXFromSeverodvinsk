// ============================================================
// QUEST 26 · ЛЕГЕНДА · Микс: HashMap + String + Stream + equals
// Уровни ROADMAP: LVL 61, повторение 🔁 всего инженерного блока
//
//   ./q 26
//
// Босс-уровень повторения: старые темы в новых комбинациях.
// Сдать тему и забыть — не выйдет: здесь всё сразу.
// Максимум: 185 XP.
// ============================================================

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Quest26_Mixed {

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

    // Ключ экипажа: равенство по id (без этого карта не найдёт копию!).
    static class CrewKey {
        final String id;

        CrewKey(String id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            // TODO 3а: ✍️ классика: this==o → true; instanceof с паттерном; сравни id
            throw new UnsupportedOperationException("3a not implemented");
        }

        @Override
        public int hashCode() {
            // TODO 3б: ✍️ return id.hashCode();
            throw new UnsupportedOperationException("3b not implemented");
        }
    }

    public static void main(String[] args) {
        System.out.println("=== QUEST 26 · МИКС ===");

        try {
            check(freqStream(List.of("a", "b", "a")).get("a") == 2L, "1 · частоты стримом", 25);
        } catch (UnsupportedOperationException e) {
            todo("1 · частоты стримом");
        }
        try {
            check(topWords(List.of("a", "b", "a", "c", "b", "a"), 2).equals(List.of("a", "b")),
                    "2 · топ-N слов", 25);
        } catch (UnsupportedOperationException e) {
            todo("2 · топ-N слов");
        }
        try {
            Map<CrewKey, String> roles = new java.util.HashMap<>();
            roles.put(new CrewKey("p1"), "пилот");
            check(roles.get(new CrewKey("p1")).equals("пилот"), "3 · equals спасает поиск", 25);
        } catch (UnsupportedOperationException e) {
            todo("3 · equals спасает поиск");
        }
        try {
            check(groupAnagrams(new String[]{"eat", "tea", "tan"}).size() == 2, "4 · анаграммы группами", 25);
        } catch (UnsupportedOperationException e) {
            todo("4 · анаграммы группами");
        }
        try {
            check(palindromes(List.of("шалаш", "ракета", "потоп")).equals(List.of("шалаш", "потоп")),
                    "5 · палиндромы стримом", 20);
        } catch (UnsupportedOperationException e) {
            todo("5 · палиндромы стримом");
        }
        try {
            check(secondMaxStream(new int[]{5, 9, 9, 3}) == 5, "6 · второй максимум стримом", 20);
        } catch (UnsupportedOperationException e) {
            todo("6 · второй максимум стримом");
        }
        try {
            check(initials(List.of("Анна Соколова", "Борис")).equals("АС,Б"), "7 · инициалы + joining", 20);
        } catch (UnsupportedOperationException e) {
            todo("7 · инициалы + joining");
        }
        try {
            String[] log = {"SUCCESS:1", "FAILED:2", "SUCCESS:3"};
            check(report(log).equals("SUCCESS=2, FAILED=1 | топ: SUCCESS"),
                    "8 · 👹 БОСС-МИКС: отчёт по логам", 25);
        } catch (UnsupportedOperationException e) {
            todo("8 · 👹 БОСС-МИКС: отчёт по логам");
        }

        System.out.println("------------------------");
        System.out.println("ИТОГО XP: " + xp + " / 185");
    }

    // 1 · Частоты слов стримом: groupingBy + counting. Подсказка: Function.identity()!
    static Map<String, Long> freqStream(List<String> words) {
        // TODO 1: ✍️ частоты слов, но стримом (как в Quest16 руками, теперь конвейером).
        // Какой коллектор считает вхождения?
        throw new UnsupportedOperationException("1 not implemented");
    }

    // 2 · Топ-N слов по частоте (при равенстве — по алфавиту, детерминизм!).
    static List<String> topWords(List<String> words, int n) {
        // TODO 2: ✍️ топ-N по частоте, при равенстве — по алфавиту. Разбей сам:
        // частоты (задача 1!) → сортировка записей → лимит → ключи.
        // .limit(n).map(Map.Entry::getKey).toList();
        throw new UnsupportedOperationException("2 not implemented");
    }

    // 4 · Сгруппируй анаграммы: ключ — отсортированные буквы слова.
    static Map<String, List<String>> groupAnagrams(String[] words) {
        // TODO 4: ✍️ ключ группы = отсортированные буквы слова. Как получить? (массив + sort + new String)
        throw new UnsupportedOperationException("4 not implemented");
    }

    // 5 · Только палиндромы. Подсказка: s.equals(new StringBuilder(s).reverse().toString()).
    static List<String> palindromes(List<String> words) {
        // TODO 5: ✍️ только палиндромы (рецепт из Quest13 B1–B2, теперь стримом). Без подсматриваний!
        throw new UnsupportedOperationException("5 not implemented");
    }

    // 6 · Второй максимум среди различных: distinct + sort desc + skip(1) + findFirst.
    static int secondMaxStream(int[] a) {
        // TODO 6: ✍️ второй максимум среди различных — стримом. Какие три операции? (уникальность, порядок, пропуск)
        throw new UnsupportedOperationException("6 not implemented");
    }

    // 7 · Инициалы каждого имени через запятую: "Анна Соколова" → "АС".
    static String initials(List<String> names) {
        // TODO 7: ✍️ разбей имя на слова, возьми первые буквы, склей запятой. Три шага!
        throw new UnsupportedOperationException("7 not implemented");
    }

    // 8 · БОСС-МИКС: "СТАТУС:номер" → частоты → строка "A=2, B=1 | топ: A".
    // Формат: ключи по алфавиту в первой части! Подсказка: собери из задач 1, 2, 7.
    static String report(String[] log) {
        // TODO 8: ✍️ частоты статусов — как в задаче 1. Потом: строка «K=V» по алфавиту + топ-ключ.
        // Формат сверь с проверкой выше!
        throw new UnsupportedOperationException("8 not implemented");
    }
}
