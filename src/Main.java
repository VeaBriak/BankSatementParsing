import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
/*Написать код парсинга банковской выписки (файл movementsList.csv).
Код должен выводить сводную информацию по этой выписке: общий приход, общий расход, а также разбивку расходов.*/

public class Main {
    private static String dataFile = "data/movementList.csv";
    private static double incomings = 0;
    private static double consumptions = 0;
    private static TreeMap<String, Double> categories = new TreeMap<>();

    public static void main(String[] args) {

        loadStatementFromFile();
        System.out.println("Общий приход: " + incomings);
        System.out.println("\nОбщий расход: " + consumptions);
        printCategories();

    }

    private static void loadStatementFromFile() {
        double consumption;
        String consumptionCategory;

        try {
            List<String> lines = Files.readAllLines(Paths.get(dataFile));
            lines.remove(0);
            for (String line : lines) {
                String[] fragments = line.split(",", 8);
                if (fragments.length != 8) {
                    System.out.println("!Wrong line: " + line);
                    continue;
                }
                incomings += Double.parseDouble(fragments[6].replace("\"", "").replace(",", "."));
                consumption = Double.parseDouble(fragments[7].replace("\"", "").replace(",", "."));
                consumptions += consumption;

                if (consumption > 0) {
                    String[] columnFragments = fragments[5].split("\\s{3,}");
                    if (columnFragments[1].lastIndexOf("\\") >= 0) {
                        consumptionCategory = columnFragments[1].substring(columnFragments[1].lastIndexOf("\\") + 1).trim().toUpperCase();
                    } else {
                        consumptionCategory = columnFragments[1].substring(columnFragments[1].lastIndexOf("/") + 1).trim().toUpperCase();
                    }
                    putCategoriesInMap(consumptionCategory, consumption);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void putCategoriesInMap(String consumptionCategory, double consumption) {
        if (categories.containsKey(consumptionCategory)) {
            categories.put(consumptionCategory, categories.get(consumptionCategory) + consumption);
        } else {
            categories.put(consumptionCategory, consumption);
        }
    }

    private static void printCategories() {
        System.out.println("\nРасходы по категориям:");
        categories.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getValue))
                .forEach(System.out::println);
    }
}
