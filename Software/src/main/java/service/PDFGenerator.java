package service;

import controller.AddController;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by next on 11/4/2017.
 */
public class PDFGenerator {

    private static String filename;

    private static PDFont font = PDType1Font.HELVETICA;
    private static PDFont boldFont = PDType1Font.HELVETICA_BOLD;

    private static PDPage singlePage;

    private static String dateFrom;
    private static String dateTo;


    private static ArrayList<String> violators;
    private static ArrayList<String> fees;


    private static File file;
    private static PDDocument doc;
    private static PDPageContentStream cs;

    private static String[][] violationsContent;
    private static String[][] feesContent;

    private static int vtotal;
    private static int vtotalc;
    private static int ftotal;

    private static int x,y;

    public PDFGenerator( File file, String dateFrom, String dateTo) throws IOException {
        this.file = file;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        singlePage = new PDPage();
        doc = new PDDocument();
        doc.addPage(singlePage);
        cs = new PDPageContentStream(doc,singlePage);
        writeHeader();
        x = 50;
        y = 630;
//        writeFooter();
    }


    public void writeHeader() throws IOException {
        PDImageXObject pdImage = PDImageXObject.createFromFile("/Users/tolapura/Desktop/LATEST HANI/src/main/resources/img/csbt.png", doc);
        cs.drawImage(pdImage,50,735,100,30);
//        cs.drawImage(pdImage,0.5f,0,singlePage.getMediaBox().getWidth()-1,singlePage.getMediaBox().getHeight()+3);

//        cs.beginText();
//        cs.setFont(boldFont,10);
//        cs.newLineAtOffset(45,728);
//        cs.showText("BUS LOADING SYSTEM");
//        cs.endText();

        cs.beginText();
        cs.setFont(boldFont,10);
        cs.newLineAtOffset(50,720);
        cs.showText("CEBU SOUTH BUS TERMINAL");
        cs.endText();

        cs.beginText();
        cs.setFont(font,10);
        cs.newLineAtOffset(50,710);
        cs.showText("N. Bacalso Avenue, Cebu City");
        cs.endText();

        cs.beginText();
        cs.newLine();
        cs.setFont(boldFont,10);
        cs.newLineAtOffset(50,680);
        cs.showText("Summary Report");
        cs.endText();

        cs.beginText();
        cs.newLine();
        cs.setFont(boldFont,10);
        cs.newLineAtOffset(50,675);
        cs.showText("_______________________________________________________________________________________");
        cs.endText();

        cs.beginText();
        cs.newLine();
        cs.setFont(font,8);
        cs.newLineAtOffset(50, 660);
        cs.showText("DATE FROM: ");
        cs.endText();

        cs.beginText();
        cs.newLine();
        cs.setFont(font,8);
        cs.newLineAtOffset(120, 660);
        cs.showText(dateFrom + "     TO: " + dateTo);
        cs.endText();

        cs.beginText();
        cs.newLine();
        cs.setFont(boldFont,10);
        cs.newLineAtOffset(50,650);
        cs.showText("_______________________________________________________________________________________");
        cs.endText();
    }


    public void writeViolationsToPDF(ArrayList<String> companies, ArrayList<Integer> count) throws IOException {
        violationsContent = new String[companies.size()][2];
        vtotal = 0;
        violators = new ArrayList<String>();
        for(int i = 0 ; i < companies.size() ; i++){
            for(int j = 0 ; j<2 ; j++){
                if(j == 0){
                    violationsContent[i][j] = companies.get(i);
                }else {
                    violationsContent[i][j] = count.get(i)+"";
                }
            }
            violators.add(companies.get(i) + "            " + count.get(i));
            vtotal += count.get(i);
        }

        y-=20;

        cs.beginText();
        cs.newLine();
        cs.setFont(boldFont,10);
        cs.newLineAtOffset(50, y);
        cs.showText("VIOLATIONS: ");
        cs.endText();

        cs.beginText();
        cs.newLine();
        cs.setFont(font,10);
        cs.newLineAtOffset(50,y-10);
        cs.showText("_______________________________________________________________________________________");
        cs.endText();

        cs.beginText();
        cs.newLine();
        cs.setFont(font,10);
        cs.newLineAtOffset(50, y-=35);
        cs.showText("BUS COMPANY            NUMBER OF VIOLATIONS");
        cs.endText();

        cs.beginText();
        cs.newLine();
        cs.setFont(font,10);
        cs.newLineAtOffset(50,y-10);
        cs.showText("_______________________________________________________________________________________");
        cs.endText();



        y-=30;
        float colWidth = 160f;
        float rowHeight = 20f;
        for(int i = 0; i < violationsContent.length; i++){
            for(int j = 0 ; j < violationsContent[i].length; j++){
                String text = violationsContent[i][j];
                cs.beginText();
                cs.moveTextPositionByAmount(x,y);
                cs.drawString(text);
                cs.endText();
                x += colWidth;
            }

            y-=rowHeight;
            x = 50;
        }

        cs.beginText();
        cs.newLine();
        cs.setFont(font,10);
        cs.newLineAtOffset(50,y-5);
        cs.showText("_______________________________________________________________________________________");
        cs.endText();

        y-=20;
        for(int j = 0 ; j < 3; j++){
            String text = "";
            if(j == 0){
                text = "TOTAL";
            }else if(j == 1){
                text = vtotal + "";
            }
            cs.beginText();
            cs.moveTextPositionByAmount(x,y);
            cs.drawString(text);
            cs.endText();
            x += colWidth;
        }
        x = 50;
//
//        cs.beginText();
//        cs.newLine();
//        cs.setFont(boldFont,10);
//        cs.newLineAtOffset(x, y-=20);
//        cs.showText("TOTAL                                             "+vtotal);
//        y-=50;
//        cs.endText();

    }

    public void writeFeesToPDF(ArrayList<String> companies, ArrayList<Integer> count) throws IOException {
        feesContent = new String[companies.size()][3];
        ftotal = 0;
        vtotalc = 0;
        fees = new ArrayList<String>();
        for(int i = 0 ; i < companies.size() ; i++){
            for(int j = 0 ; j<3 ; j++){
                if(j == 0){
                    feesContent[i][j] = companies.get(i);
                }else if(j==1){
                    feesContent[i][j] = count.get(i)+"";
                } else{
                    feesContent[i][j] = (count.get(i)*200)+"";
                }
            }
            fees.add(companies.get(i) + "                  " + count.get(i) + "              " + count.get(i)*200);
            ftotal += (count.get(i)*200);
            vtotalc+=count.get(i);
        }

        cs.beginText();
        cs.newLine();
        cs.setFont(boldFont,11);
        cs.newLineAtOffset(x, y-=20);
        cs.showText("FEES: ");
        cs.endText();

        cs.beginText();
        cs.newLine();
        cs.setFont(boldFont,10);
        cs.newLineAtOffset(50,y-10);
        cs.showText("_______________________________________________________________________________________");
        cs.endText();

        cs.beginText();
        cs.newLine();
        cs.setFont(font,10);
        cs.newLineAtOffset(x, y-=35);
        cs.showText("BUS COMPANY                      NUMBER OF BUSES                           AMOUNT PAID");
        cs.endText();

        cs.beginText();
        cs.newLine();
        cs.setFont(font,10);
        cs.newLineAtOffset(50,y-10);
        cs.showText("_______________________________________________________________________________________");
        cs.endText();



        y-=30;
        float colWidth = 160f;
        float rowHeight = 20f;
        for(int i = 0; i < feesContent.length; i++){
            for(int j = 0 ; j < feesContent[i].length; j++){
                String text = feesContent[i][j];
                cs.beginText();
                cs.moveTextPositionByAmount(x,y);
                cs.drawString(text);
                cs.endText();
                x += colWidth;
            }
            y-=rowHeight;
            x = 50;
            if(y<=100){
                cs.close();
                singlePage = new PDPage();
                doc.addPage(singlePage);
                cs = new PDPageContentStream(doc,singlePage);
                writeHeader();
                y = 630;
            }
        }

        cs.beginText();
        cs.newLine();
        cs.setFont(font,10);
        cs.newLineAtOffset(x,y-5);
        cs.showText("_______________________________________________________________________________________");
        cs.endText();

        y-=20;
        for(int j = 0 ; j < 3; j++){
            String text = "";
            if(j == 0){
                text = "TOTAL";
            }else if(j == 1){
                text = vtotalc + "";
            }else{
                text = ftotal + "";
            }
            cs.beginText();
            cs.moveTextPositionByAmount(x,y);
            cs.drawString(text);
            cs.endText();
            x += colWidth;
        }

        x = 50;
//
//        cs.beginText();
//        cs.newLine();
//        cs.setFont(boldFont,10);
//        cs.newLineAtOffset(x, y-=20);
//        cs.showText("TOTAL                                              " + vtotalc+ "                                                     "+ ftotal);
//        y-=50;
//        cs.endText();

    }

    public void testMultiPage() throws IOException {

        float docHeight = singlePage.getMediaBox().getHeight();
        for(int i = 0 ; i <200 ; i++){
            cs.beginText();
            cs.newLineAtOffset(x,y);
            cs.setFont(font,10);
            cs.showText(i + " ");
            cs.newLine();
            cs.endText();
            y-=10;
            if(y<=100){
                cs.close();
                singlePage = new PDPage();
                doc.addPage(singlePage);
                cs = new PDPageContentStream(doc,singlePage);
                writeHeader();
                y = 630;
            }
        }

    }

    public void writeFooter() throws IOException {
        cs.beginText();
        cs.newLineAtOffset(x,100);
        cs.setFont(font,10);
        cs.showText("-------------------- footer space --------------------------");
        cs.newLine();
        cs.endText();
    }

    public void savePDF() throws IOException {
        cs.close();
        doc.save(file);
        doc.close();
        AddController.successmessage("SUCCESSFULLY SAVED! ");
    }





}
