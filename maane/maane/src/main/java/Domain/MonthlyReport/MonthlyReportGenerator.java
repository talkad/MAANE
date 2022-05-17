package Domain.MonthlyReport;


import Domain.CommonClasses.Response;
import Domain.UsersManagment.UserController;

import java.io.File;
import java.io.FileInputStream;

public class MonthlyReportGenerator {

    private UserController userController;
    private MSWordWriterService writerService;

    private static class CreateSafeThreadSingleton {
        private static final MonthlyReportGenerator INSTANCE = new MonthlyReportGenerator();
    }

    public static MonthlyReportGenerator getInstance() {
        return MonthlyReportGenerator.CreateSafeThreadSingleton.INSTANCE;
    }

    public MonthlyReportGenerator() {
        this.userController = UserController.getInstance();
        this.writerService = new MSWordWriterService();
    }

    public Response<byte[]> generateMonthlyReport(String username) {

        Response<File> reportRes = writerService.createDoc("maane\\src\\main\\resources\\monthlyReport_" + username + ".docx");
        byte[] binaryFile = null;
        FileInputStream fileInputStream;
        File file = reportRes.getResult();

        // receive data from userController

        if(!reportRes.isFailure()) {
            binaryFile = new byte[(int)file.length()];

            try {
                fileInputStream = new FileInputStream(file);

                fileInputStream.read(binaryFile);
                fileInputStream.close();
            } catch(Exception e){
                return new Response<>(null, true, e.getMessage());
            }
        }

        // delete file from memory
        if(file != null)
            file.delete();

        return new Response<>(binaryFile, reportRes.isFailure(), reportRes.getErrMsg());
    }


//    public static void main(String[] args)  {
//
//        MSWordWriterService service = new MSWordWriterService();
//
//        service.createDoc("maane\\src\\main\\resources\\monthlyReport.docx");
//
//
//    }

}
