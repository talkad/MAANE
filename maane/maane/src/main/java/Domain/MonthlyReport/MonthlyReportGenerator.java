package Domain.MonthlyReport;


public class MonthlyReportGenerator {

    public static void main(String[] args)  {

        MSWordWriterService service = new MSWordWriterService();

        service.createDoc("maane\\src\\main\\resources\\monthlyReport.docx");


    }

}
