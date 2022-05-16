package Domain.MonthlyReport;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;

import java.io.File;

public class MonthlyReportGenerator {

    public static void main(String[] args) throws Docx4JException {
        WordprocessingMLPackage wordPackage = WordprocessingMLPackage.createPackage();
        MainDocumentPart mainDocumentPart = wordPackage.getMainDocumentPart();
        mainDocumentPart.addStyledParagraphOfText("Title", "Hello World!");
        mainDocumentPart.addParagraphOfText("Welcome To Baeldung");

//        int writableWidthTwips = wordPackage.getDocumentModel()
//                .getSections().get(0).getPageDimensions().getWritableWidthTwips();
//        int columnNumber = 3;
//        Tbl tbl = TblFactory.createTable(3, 3, writableWidthTwips/columnNumber);
//        List<Object> rows = tbl.getContent();
//        for (Object row : rows) {
//            Tr tr = (Tr) row;
//            List<Object> cells = tr.getContent();
//            for(Object cell : cells) {
//                Tc td = (Tc) cell;
//                td.getContent().add(p);
//            }
//        }

        File exportFile = new File("welcome.docx");
        wordPackage.save(exportFile);


    }

}
