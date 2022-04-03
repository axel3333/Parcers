import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;


public class Main {

    public static List<Employee> parseCSV(String[] mapping, String name) {
        List<Employee> staff = null;
        try (CSVReader reader = new CSVReader(new FileReader(name))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(mapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            staff = csv.parse();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return staff;
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    public static void writeString(String json, String fileName) {
        try (FileWriter writer = new FileWriter(fileName, false)) {
            writer.write(json);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Employee> parseXML(String fileName) {
        List<Employee> employee = new ArrayList<>();
       try {
           DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
           DocumentBuilder builder = factory.newDocumentBuilder();
           Document document = builder.parse(new File (fileName));

           Node root = document.getDocumentElement();
           NodeList nodeList = root.getChildNodes();
           for (int i = 0; i < nodeList.getLength(); i++) {
               Node node_ = nodeList.item(i);
               if(root.ELEMENT_NODE == node_.getNodeType()) {
                   Element element = (Element) node_;
                   NamedNodeMap map = element.getAttributes();
                   long id = Long.parseLong(element.getAttribute("id"));
                   String firstName = element.getAttribute("firstName");
                   String lastName = element.getAttribute("lastName");
                   String country = element.getAttribute("country");
                   int age = Integer.parseInt(element.getAttribute("age"));
                   Employee emp = new Employee(id, firstName, lastName, country, age);
                    employee.add(emp);
               }
           }

       } catch (ParserConfigurationException | IOException | SAXException e) {
           e.printStackTrace();
       }
        return employee;
    }

    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        String fileNameXML = "data.xml";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, "data.json");
        List<Employee> list2 = parseXML(fileNameXML);
        String jsonXml = listToJson(list2);
        writeString(jsonXml, "dataXML.json");
    }
}
