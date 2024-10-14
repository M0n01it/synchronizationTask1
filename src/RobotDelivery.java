import java.util.*;
import java.util.concurrent.CountDownLatch;

public class RobotDelivery {

    // Статическая карта для хранения частот количества 'R'
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    // Метод для генерации маршрута
    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

    // Основной метод
    public static void main(String[] args) {
        final int THREAD_COUNT = 1000;
        final String LETTERS = "RLRFR";
        final int ROUTE_LENGTH = 100;

        // Латч для ожидания завершения всех потоков
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        for (int i = 0; i < THREAD_COUNT; i++) {
            Thread thread = new Thread(() -> {
                // Генерация маршрута
                String route = generateRoute(LETTERS, ROUTE_LENGTH);
                // Подсчет количества 'R'
                int countR = countOccurrences(route, 'R');
                // Вывод результата текущего потока (опционально)
                // System.out.println("Количество 'R' в маршруте: " + countR);

                // Обновление карты частот с синхронизацией
                synchronized (sizeToFreq) {
                    sizeToFreq.put(countR, sizeToFreq.getOrDefault(countR, 0) + 1);
                }
                // Уменьшаем счетчик латча
                latch.countDown();
            });
            thread.start();
        }

        // Ожидание завершения всех потоков
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Анализ результатов
        analyzeResults();
    }

    // Метод для подсчета вхождений символа
    public static int countOccurrences(String str, char ch) {
        int count = 0;
        for(char c : str.toCharArray()) {
            if(c == ch) count++;
        }
        return count;
    }

    // Метод для анализа и вывода результатов
    public static void analyzeResults() {
        if (sizeToFreq.isEmpty()) {
            System.out.println("Нет данных для отображения.");
            return;
        }

        // Поиск наиболее частого количества 'R'
        int mostFrequentCount = -1;
        int maxFrequency = -1;
        for (Map.Entry<Integer, Integer> entry : sizeToFreq.entrySet()) {
            if (entry.getValue() > maxFrequency) {
                maxFrequency = entry.getValue();
                mostFrequentCount = entry.getKey();
            }
        }

        System.out.println("Самое частое количество повторений: " + mostFrequentCount + " (встретилось " + maxFrequency + " раз)");

        // Удаляем самое частое количество из карты для отображения остальных
        Map<Integer, Integer> otherFrequencies = new HashMap<>(sizeToFreq);
        otherFrequencies.remove(mostFrequentCount);

        if (!otherFrequencies.isEmpty()) {
            System.out.println("Другие размеры:");
            // Сортируем оставшиеся ключи для удобства чтения
            List<Integer> sortedKeys = new ArrayList<>(otherFrequencies.keySet());
            Collections.sort(sortedKeys);
            for (Integer key : sortedKeys) {
                System.out.println("- " + key + " (" + otherFrequencies.get(key) + " раз)");
            }
        }
    }
}