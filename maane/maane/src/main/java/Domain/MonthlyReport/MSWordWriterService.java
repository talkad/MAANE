package Domain.MonthlyReport;

import Domain.CommonClasses.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class MSWordWriterService {

    /**
     * create docx file according to monthly report template
     * @param filepath the location the file will be saved and manupulated
     * @return response if the function succeeded
     */
    public Response<File> createDoc(String filepath) {
        XWPFDocument document = new XWPFDocument();

        setPageSize(document, 15840, 12240);
        addFooter(document);
        addHeader(document);
        addUserDetailsTable(document);
        document.createParagraph();
        addActivityTable(document);
        document.createParagraph();
        addApprovalTable(document);

        try {
            document.write(new FileOutputStream(filepath));
            document.close();
        } catch(IOException e){
            return new Response<>(null, true, e.getMessage());
        }

        return new Response<>(new File(filepath), false, "monthly report generated successfully");
    }


    /**
     * Set page size to given width and height
     */
    private void setPageSize(XWPFDocument document, int width, int height){
        CTDocument1 doc = document.getDocument();
        CTBody body = doc.getBody();

        if (!body.isSetSectPr()) {
            body.addNewSectPr();
        }
        CTSectPr section = body.getSectPr();

        if(!section.isSetPgSz()) {
            section.addNewPgSz();
        }
        CTPageSz pageSize = section.getPgSz();

        pageSize.setW(BigInteger.valueOf(width));
        pageSize.setH(BigInteger.valueOf(height));
    }

    private void addFooter(XWPFDocument document) {
        XWPFHeader header = document.createHeader(HeaderFooterType.DEFAULT);
        XWPFParagraph paragraph = header.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText("מדינת ישראל");
        run.addBreak();
        run.setText(" משרד החינוך");
        run.setFontFamily("Arial");
        run.setFontSize(12);
        run.setBold(true);
        paragraph.setAlignment(ParagraphAlignment.CENTER);
    }

    private void addHeader(XWPFDocument document) {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText("דוח עבודה חדשי במסגרת ההדרכה לעובדי הוראה/גני ילדים: מורה בתפקיד הדרכה במחוז");
        run.setFontFamily("David");
        run.setFontSize(14);
        run.setBold(true);
        paragraph.setAlignment(ParagraphAlignment.CENTER);
    }

    public void addUserDetailsTable(XWPFDocument document) {
        XWPFTable table = document.createTable(2, 32);
        table.setWidth(13500);

        // first row
        table.getRow(0).setHeight(500);

        mergeTableCellsHorizontal(table,0, 0, 3);
        writeIntoTableCell(table.getRow(0).getCell(0), "מקום המגורים", "", true, ParagraphAlignment.RIGHT, 12);

        mergeTableCellsHorizontal(table,0, 3, 5);
        writeIntoTableCell(table.getRow(0).getCell(3), "השנה", "", true, ParagraphAlignment.RIGHT, 12);

        writeIntoTableCell(table.getRow(0).getCell(5), "החודש", "", true, ParagraphAlignment.RIGHT, 12);

        for(int i = 6; i < 15; i++)
            table.getRow(0).getCell(i).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(300));

        writeIntoTableCell(table.getRow(0).getCell(15), "מס ת\"ז", "", true, ParagraphAlignment.RIGHT, 12);

        mergeTableCellsHorizontal(table,0, 16, 24);
        writeIntoTableCell(table.getRow(0).getCell(16), "השם הפרטי", "", true, ParagraphAlignment.RIGHT, 12);

        mergeTableCellsHorizontal(table,0, 24, 32);
        writeIntoTableCell(table.getRow(0).getCell(24), "שם המשפחה", "", true, ParagraphAlignment.RIGHT, 12);


        // second row
        table.getRow(1).setHeight(1000);

        mergeTableCellsHorizontal(table,1, 0, 5);
        writeIntoTableCell(table.getRow(1).getCell(0), "היחידה/המחוז ", "המינהל לחינוך התיישבותי", true, ParagraphAlignment.RIGHT, 12);

        mergeTableCellsHorizontal(table,1, 5, 17);
        writeIntoTableCell(table.getRow(1).getCell(5), ":נושא ההדרכה", "", true, ParagraphAlignment.RIGHT, 12);

        writeIntoTableCell(table.getRow(1).getCell(22), "א", "O", true, ParagraphAlignment.CENTER, 12);
        writeIntoTableCell(table.getRow(1).getCell(21), "ב", "O", true, ParagraphAlignment.CENTER, 12);
        writeIntoTableCell(table.getRow(1).getCell(20), "ג", "O", true, ParagraphAlignment.CENTER, 12);
        writeIntoTableCell(table.getRow(1).getCell(19), "ד", "O", true, ParagraphAlignment.CENTER, 12);
        writeIntoTableCell(table.getRow(1).getCell(18), "ה", "O", true, ParagraphAlignment.CENTER, 12);
        writeIntoTableCell(table.getRow(1).getCell(17), "ו", "O", true, ParagraphAlignment.CENTER, 12);

        mergeTableCellsHorizontal(table,1, 23, 27);
        writeIntoTableCell(table.getRow(1).getCell(23), "היום בשבוע", "(יש לסמן X)", true, ParagraphAlignment.CENTER, 12);

        mergeTableCellsHorizontal(table,1, 27, 32);
        writeIntoTableCell(table.getRow(1).getCell(27), "מספר ימי ההדרכה", "", true, ParagraphAlignment.RIGHT, 12);

    }

    private void writeIntoTableCell(XWPFTableCell cell, String text, String subText,  boolean bold, ParagraphAlignment alignment, int fontSize) {
        XWPFParagraph paragraph = cell.getParagraphs().get(0);
        XWPFRun run = paragraph.createRun();

        run.setFontFamily("David");
        run.setFontSize(fontSize);
        paragraph.setAlignment(alignment);
        run.setBold(bold);
        run.setText(text);

        if(subText.length() > 0) {
            run.addBreak();
            run.addBreak();
            run.setText(subText);
        }

    }


    private void addApprovalTable(XWPFDocument document) {

        XWPFTable table = document.createTable(1, 2);
        table.setWidth(13500);

        XWPFTableCell cell = table.getRow(0).getCell(0);

        XWPFParagraph paragraph = cell.getParagraphs().get(0);
        XWPFRun run = paragraph.createRun();

        run.setFontFamily("David");
        run.setFontSize(10);
        paragraph.setAlignment(ParagraphAlignment.RIGHT);
        run.setText(":הריני מאשר שהעובד הועסק ונסע באישור בהתאם לרשום לעיל" );
        run.addBreak();
        run.setText(" נסע בתפקיד כרשום לעיל - " );
        run.addBreak();
        run.setText(" מגיעות לו הוצאות אש\"ל ודמי כלכלה - " );

        paragraph = cell.addParagraph();
        run = paragraph.createRun();

        run.setFontFamily("David");
        run.setFontSize(12);
        paragraph.setAlignment(ParagraphAlignment.RIGHT);
        run.setText(" __________ :תאריך    _________ :שם  _______________ :חתימה  ");


        cell = table.getRow(0).getCell(1);

        paragraph = cell.getParagraphs().get(0);
        run = paragraph.createRun();

        run.setFontFamily("David");
        run.setFontSize(10);
        paragraph.setAlignment(ParagraphAlignment.RIGHT);
        run.setText(".אני מצהיר בזאת כי בתאריכים הנ\"ל עבדתי ונסעתי בהתאם לרשום לעיל" );
        run.addBreak();
        run.setText(".האש\"ל והוצאות הנסיעה הנדרשים על ידי לעיל לא שולמו לי ולא נדרשו משום גוף נוסף" );
        run.addBreak();

        paragraph = cell.addParagraph();
        run = paragraph.createRun();

        run.setFontFamily("David");
        run.setFontSize(12);
        paragraph.setAlignment(ParagraphAlignment.RIGHT);
        run.setText("______________:" + "תאריך: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "   חתימה ");

    }


    public void addActivityTable(XWPFDocument document) {
        XWPFTable table = document.createTable(10, 17);
        table.setWidth(13500);

        // first row
        mergeTableCellsHorizontal(table,0, 0, 3);
        writeIntoTableCell(table.getRow(0).getCell(0), "אש\"ל ודמי כלכלה", "", true, ParagraphAlignment.CENTER, 10);

        mergeTableCellsHorizontal(table,0, 3, 5);
        writeIntoTableCell(table.getRow(0).getCell(3), "דמי הנסיעה ברכב ציבורי", "", true, ParagraphAlignment.CENTER, 12);

        mergeTableCellsHorizontal(table,0, 5, 7);
        writeIntoTableCell(table.getRow(0).getCell(5), "המרחק בקילומטרים", "", true, ParagraphAlignment.CENTER, 12);

        mergeTableCellsHorizontal(table,0, 7, 9);
        writeIntoTableCell(table.getRow(0).getCell(7), "יעדי הנסיעה", "", true, ParagraphAlignment.CENTER, 12);

        writeIntoTableCell(table.getRow(0).getCell(9), "הצוות המודרך", "", true, ParagraphAlignment.CENTER, 12);

        writeIntoTableCell(table.getRow(0).getCell(10), "מקום הפעילות/שם בית הספר", "", true, ParagraphAlignment.CENTER, 12);

        writeIntoTableCell(table.getRow(0).getCell(11), "תיאור הפעילות", "", true, ParagraphAlignment.CENTER, 12);

        writeIntoTableCell(table.getRow(0).getCell(12), "סה\"כ השעות", "", true, ParagraphAlignment.CENTER, 12);

        mergeTableCellsHorizontal(table,0, 13, 15);
        writeIntoTableCell(table.getRow(0).getCell(13), "שעות העבודה", "", true, ParagraphAlignment.CENTER, 12);

        writeIntoTableCell(table.getRow(0).getCell(15), "תאריך", "", false, ParagraphAlignment.CENTER, 10);

        writeIntoTableCell(table.getRow(0).getCell(16), "יום", "", false, ParagraphAlignment.CENTER, 10);


        // second row
        writeIntoTableCell(table.getRow(1).getCell(0), "ערב", "", false, ParagraphAlignment.CENTER, 10);
        writeIntoTableCell(table.getRow(1).getCell(1), "צהריים", "", false, ParagraphAlignment.CENTER, 10);
        writeIntoTableCell(table.getRow(1).getCell(2), "בוקר", "", false, ParagraphAlignment.CENTER, 10);

        writeIntoTableCell(table.getRow(1).getCell(3), "עירוני", "", false, ParagraphAlignment.CENTER, 10);
        writeIntoTableCell(table.getRow(1).getCell(4), "בינעירוני", "", false, ParagraphAlignment.CENTER, 10);

        writeIntoTableCell(table.getRow(1).getCell(5), "דרכים קשות", "", false, ParagraphAlignment.CENTER, 10);
        writeIntoTableCell(table.getRow(1).getCell(6), "דרכים רגילות", "", false, ParagraphAlignment.CENTER, 10);

        writeIntoTableCell(table.getRow(1).getCell(7), "עד מקום", "", false, ParagraphAlignment.CENTER, 10);
        writeIntoTableCell(table.getRow(1).getCell(8), "ממקום", "", false, ParagraphAlignment.CENTER, 10);

        writeIntoTableCell(table.getRow(1).getCell(13), "עד שעה", "", false, ParagraphAlignment.CENTER, 10);
        writeIntoTableCell(table.getRow(1).getCell(14), "משעה", "", false, ParagraphAlignment.CENTER, 10);

        // merging columns
        mergeTableCellsVertical(table,9);
        mergeTableCellsVertical(table,10);
        mergeTableCellsVertical(table,11);
        mergeTableCellsVertical(table,12);
        mergeTableCellsVertical(table,15);
        mergeTableCellsVertical(table,16);

    }

    private void mergeTableCellsHorizontal(XWPFTable table, int rowIdx, int startColIdx, int endColIdx) {

        for(int i = startColIdx + 1; i < endColIdx; i++) {
            // first column
            CTHMerge hMerge = CTHMerge.Factory.newInstance();
            hMerge.setVal(STMerge.RESTART);
            XWPFTableCell cell = table.getRow(rowIdx).getCell(startColIdx);

            if (cell.getCTTc().getTcPr() == null)
                cell.getCTTc().addNewTcPr();

            cell.getCTTc().getTcPr().setHMerge(hMerge);

            // the collapsed  column
            CTHMerge hMerge1 = CTHMerge.Factory.newInstance();
            hMerge1.setVal(STMerge.CONTINUE);
            cell = table.getRow(rowIdx).getCell(i);

            if (cell.getCTTc().getTcPr() == null)
                cell.getCTTc().addNewTcPr();

            cell.getCTTc().getTcPr().setHMerge(hMerge1);
        }

    }

    private void mergeTableCellsVertical(XWPFTable table, int colIdx) {

        // first column
        CTVMerge vMerge = CTVMerge.Factory.newInstance();
        vMerge.setVal(STMerge.RESTART);
        XWPFTableCell cell = table.getRow(0).getCell(colIdx);

        if (cell.getCTTc().getTcPr() == null)
            cell.getCTTc().addNewTcPr();

        cell.getCTTc().getTcPr().setVMerge(vMerge);

        // the collapsed  column
        CTVMerge  vMerge1  = CTVMerge.Factory.newInstance();
        vMerge1.setVal(STMerge.CONTINUE);
        cell = table.getRow(1).getCell(colIdx);

        if (cell.getCTTc().getTcPr() == null)
            cell.getCTTc().addNewTcPr();

        cell.getCTTc().getTcPr().setVMerge(vMerge1);

    }


}