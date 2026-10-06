// ============================================================
// QUEST 24 · КОМАНДОР · Потоки без зависаний: join, deadlock, Future
// Уровни ROADMAP: LVL 47В, 47Г, 48Б, 49Б, 50Б (босс-дедлок), 51В, 51Г
//
//   ./q 24
//
// Все демо детерминированы и безопасны: дедлок — на daemon-потоках
// (JVM всё равно завершится), пулы гасятся в finally, таймауты везде.
// Максимум: 170 XP.
// ============================================================

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class Quest24_Threads {

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

    public static void main(String[] args) throws Exception {
        System.out.println("=== QUEST 24 · ПОТОКИ ===");

        try {
            check(joinOrder().equals(List.of(1, 2)), "1 · join: порядок гарантирован", 20);
        } catch (UnsupportedOperationException e) {
            todo("1 · join: порядок гарантирован");
        }
        try {
            check(atomicCounter() == 100_000, "2 · AtomicInteger: всегда точно", 20);
        } catch (UnsupportedOperationException e) {
            todo("2 · AtomicInteger: всегда точно");
        }
        try {
            check(syncCounter() == 100_000, "3 · synchronized: тоже точно", 20);
        } catch (UnsupportedOperationException e) {
            todo("3 · synchronized: тоже точно");
        }
        try {
            check(deadlockStuck(), "4 · дедлок ВИСИТ (daemon, JVM жива)", 25);
        } catch (UnsupportedOperationException e) {
            todo("4 · дедлок ВИСИТ (daemon, JVM жива)");
        }
        try {
            check(fixedOrder(), "5 · один порядок замков — нет дедлока", 25);
        } catch (UnsupportedOperationException e) {
            todo("5 · один порядок замков — нет дедлока");
        }
        try {
            check(futureTimeout().equals("timeout"), "6 · Future.get с таймаутом", 20);
        } catch (UnsupportedOperationException e) {
            todo("6 · Future.get с таймаутом");
        }
        try {
            check(completableChain() == 42, "7 · CompletableFuture-цепочка", 20);
        } catch (UnsupportedOperationException e) {
            todo("7 · CompletableFuture-цепочка");
        }
        try {
            check(mapCounter() == 40_000, "8 · ConcurrentHashMap: точно", 20);
        } catch (UnsupportedOperationException e) {
            todo("8 · ConcurrentHashMap: точно");
        }

        System.out.println("------------------------");
        System.out.println("ИТОГО XP: " + xp + " / 170");
    }

    // 1 · Запусти t1, ДОЖДИСЬ join, потом t2 + join. Порядок [1, 2] гарантирован!
    // Список общий — оберни в synchronizedList (потом поймёшь, почему это важно).
    static List<Integer> joinOrder() throws Exception {
        // TODO 1: ✍️ List<Integer> log = Collections.synchronizedList(new ArrayList<>());
        // Thread t1 = new Thread(() -> log.add(1)); t1.start(); t1.join();
        // Thread t2 = new Thread(() -> log.add(2)); t2.start(); t2.join(); return log;
        throw new UnsupportedOperationException("1 not implemented");
    }

    // 2 · Два потока по 50_000 инкрементов AtomicInteger + join обоих. Всегда ровно 100_000!
    // ЭКСПЕРИМЕНТ (после зелёного!): замени AtomicInteger на int (и incrementAndGet на ++) —
    // запусти 5 раз, запиши цифры. Видишь потери? Вот она, гонка. ВЕРНИ атомик обратно!
    static int atomicCounter() throws Exception {
        // TODO 2: ✍️ AtomicInteger c = new AtomicInteger(); Runnable r = () -> {
        // for (int i = 0; i < 50_000; i++) c.incrementAndGet(); };
        // Thread a = new Thread(r), b = new Thread(r); a.start(); b.start(); a.join(); b.join();
        // return c.get();
        throw new UnsupportedOperationException("2 not implemented");
    }

    static int shared = 0;

    static synchronized void bump() {
        shared++;
    }

    // 3 · То же через synchronized-метод bump(). Сбрось shared = 0 в начале!
    // ЭКСПЕРИМЕНТ: убери synchronized у bump — запусти 5 раз, сравни с задачей 2. ВЕРНИ обратно!
    static int syncCounter() throws Exception {
        // TODO 3: ✍️ shared = 0; два потока по 50_000 вызовов bump() + join обоих; return shared;
        throw new UnsupportedOperationException("3 not implemented");
    }

    static final Object LOCK_A = new Object();
    static final Object LOCK_B = new Object();

    static void sleepQuietly(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    static Thread daemon(Runnable r) {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    }

    // 4 · НАСТОЯЩИЙ ДЕДЛОК: t1 берёт A→B, t2 берёт B→A (со sleep между захватами —
    // deadlock детерминирован!). Потоки daemon — JVM завершится. Верни true,
    // если через 2 секунды ОБА ещё живы (join с таймаутом!).
    static boolean deadlockStuck() throws Exception {
        // TODO 4: ✍️ Thread t1 = daemon(() -> { synchronized (LOCK_A) {
        // sleepQuietly(200); synchronized (LOCK_B) { } } });
        // Thread t2 = daemon(() -> { synchronized (LOCK_B) {
        // sleepQuietly(200); synchronized (LOCK_A) { } } });
        // t1.start(); t2.start(); t1.join(2000); t2.join(2000);
        // return t1.isAlive() && t2.isAlive();
        throw new UnsupportedOperationException("4 not implemented");
    }

    // 5 · ЧИНИМ: оба берут A→B. Верни true, если оба завершились за 2 секунды.
    static boolean fixedOrder() throws Exception {
        // TODO 5: ✍️ как в 4, но ОБА потока: synchronized A { sleep; synchronized B { } }.
        // join(2000) обоих; return !t1.isAlive() && !t2.isAlive();
        throw new UnsupportedOperationException("5 not implemented");
    }

    // 6 · Задача спит 5 секунд, ждём 200 мс → TimeoutException → верни "timeout".
    // Пул ГАСИ в finally (shutdown!), иначе JVM повиснет на не-daemon потоке!
    static String futureTimeout() {
        // TODO 6: ✍️ ExecutorService pool = Executors.newSingleThreadExecutor();
        // try { Future<String> f = pool.submit(() -> { sleepQuietly(5000); return "готово"; });
        // try { return f.get(200, TimeUnit.MILLISECONDS); }
        // catch (TimeoutException e) { return "timeout"; } }
        // catch (InterruptedException | ExecutionException e) { return "error"; }
        // finally { pool.shutdown(); }
        throw new UnsupportedOperationException("6 not implemented");
    }

    // 7 · supplyAsync(6).thenApply(x -> x * 7).join() → 42. Одна строка сути!
    static int completableChain() {
        // TODO 7: ✍️ return CompletableFuture.supplyAsync(() -> 6).thenApply(x -> x * 7).join();
        throw new UnsupportedOperationException("7 not implemented");
    }

    // 8 · 4 потока × 10_000 computeIfAbsent+инкремент? Проще: merge(key, 1, Integer::sum).
    // Ключ один ("полёты"), join всех, верни map.get. Всегда ровно 40_000!
    static int mapCounter() throws Exception {
        // TODO 8: ✍️ ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        // Runnable r = () -> { for (int i = 0; i < 10_000; i++) map.merge("полёты", 1, Integer::sum); };
        // 4 потока + join всех; return map.get("полёты");
        throw new UnsupportedOperationException("8 not implemented");
    }
}
