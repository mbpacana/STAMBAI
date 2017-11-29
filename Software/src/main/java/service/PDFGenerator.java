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

    private static PDFont font = PDType1Font.COURIER;
    private static PDFont boldFont = PDType1Font.COURIER_BOLD;

    private static PDPage singlePage;

    private static String dateFrom;
    private static String dateTo;

    private static ArrayList<String> companies;
    private static ArrayList<Integer> count;

    private static ArrayList<String> violators;
    private static ArrayList<String> fees;

    private static Boolean isFees;
    private static Boolean isViolation;

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
        PDImageXObject pdImage = PDImageXObject.createFromFile("/Users/tolapura/Desktop/128 3/src/main/resources/img/csbt.png", doc);
        cs.drawImage(pdImage,50,735,100,30);
        writeHeader();
        x = 100;
        y = 670;
    }


    public void writeHeader() throws IOException {
        cs.beginText();
        cs.setFont(boldFont,15);
        cs.newLineAtOffset(160,750);
        cs.showText("CEBU SOUTH BUS TERMINAL");
        cs.endText();

        cs.beginText();
        cs.newLine();
        cs.setFont(font,10);
        cs.newLineAtOffset(160,738);
        cs.showText("Summary Report");
        cs.endText();

        cs.beginText();
        cs.newLine();
        cs.setFont(boldFont,10);
        cs.newLineAtOffset(90, 710);
        cs.showText("FROM: ");
        cs.endText();

        cs.beginText();
        cs.newLine();
        cs.setFont(boldFont,10);
        cs.newLineAtOffset(130, 710);
        cs.showText(dateFrom + "     TO: " + dateTo);
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


        cs.beginText();
        cs.newLine();
        cs.setFont(boldFont,11);
        cs.newLineAtOffset(x, y);
        cs.showText("VIOLATIONS: ");
        cs.endText();

        cs.beginText();
        cs.newLine();
        cs.setFont(font,10);
        cs.newLineAtOffset(x, y-=35);
        cs.showText("BUS COMPANY            NUMBER OF VIOLATIONS");
        cs.endText();


//            for(String violator: violators){
//                cs.beginText();
//                cs.newLine();
//                cs.setFont(font,10);
//                cs.newLineAtOffset(x, y-=20);
//                cs.showText(violator);
//                y-=20;
//                cs.endText();
//            }

        y-=20;
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
            x = 100;
        }

        cs.beginText();
        cs.newLine();
        cs.setFont(font,10);
        cs.newLineAtOffset(x, y);
        cs.showText("                        _______________________");
        cs.endText();

        cs.beginText();
        cs.newLine();
        cs.setFont(boldFont,10);
        cs.newLineAtOffset(x, y-=20);
        cs.showText("TOTAL                      "+vtotal);
        y-=50;
        cs.endText();

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
            fees.add(companies.get(i) + "                  " + count.get(i) + "                         " + count.get(i)*200);
            ftotal += (count.get(i)*200);
            vtotalc+=count.get(i);
        }

        cs.beginText();
        cs.newLine();
        cs.setFont(boldFont,11);
        cs.newLineAtOffset(90, y);
        cs.showText("FEES: ");
        cs.endText();

        cs.beginText();
        cs.newLine();
        cs.setFont(font,10);
        cs.newLineAtOffset(90, y-=35);
        cs.showText("BUS COMPANY           NUM. OF BUSES                AMOUNT PAID");
        cs.endText();

//
//            for(String fee: fees){
//                cs.beginText();
//                cs.newLine();
//                cs.setFont(font,10);
//                cs.newLineAtOffset(x, y-=20);
//                cs.showText(fee);
//                y-=20;
//                cs.endText();
//            }


        y-=20;
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
            x = 100;
        }

        cs.beginText();
        cs.newLine();
        cs.setFont(font,10);
        cs.newLineAtOffset(x, y);
        cs.showText("                  _______________________________________________");
        cs.endText();

        cs.beginText();
        cs.newLine();
        cs.setFont(boldFont,10);
        cs.newLineAtOffset(x, y-=20);
        cs.showText("TOTAL                      " + vtotalc+ "                         "+ ftotal);
        y-=50;
        cs.endText();

    }


    public void savePDF() throws IOException {
        cs.close();
        doc.save(file);
        doc.close();
        AddController.successmessage("SUCCESSFULLY SAVED! ");
    }


    /**
     * @param page
     * @param contentStream
     * @param y the y-coordinate of the first row
     * @param margin the padding on left and right of table
     * @param content a 2d array containing the table data
     * @throws IOException
     */
    public static void drawTable(PDPage page, PDPageContentStream contentStream,
                                 float y, float margin,
                                 String[][] content) throws IOException {
        final int rows = content.length;
        final int cols = content[0].length;
        final float rowHeight = 20f;
        final float tableWidth = page.getMediaBox().getWidth()-(2*margin);
        final float tableHeight = rowHeight * rows;
        final float colWidth = tableWidth/(float)cols;
        final float cellMargin=5f;

        //draw the rows
        float nexty = y ;
        for (int i = 0; i <= rows; i++) {
            contentStream.drawLine(margin,nexty,margin+tableWidth,nexty);
            nexty-= rowHeight;
        }

        //draw the columns
        float nextx = margin;
        for (int i = 0; i <= cols; i++) {
            contentStream.drawLine(nextx,y,nextx,y-tableHeight);
            nextx += colWidth;
        }

        //now add the text
        contentStream.setFont(PDType1Font.HELVETICA_BOLD,12);

        float textx = margin+cellMargin;
        float texty = y-15;
        for(int i = 0; i < content.length; i++){
            for(int j = 0 ; j < content[i].length; j++){
                String text = content[i][j];
                contentStream.beginText();
                contentStream.moveTextPositionByAmount(textx,texty);
                contentStream.drawString(text);
                contentStream.endText();
                textx += colWidth;
            }
            texty-=rowHeight;
            textx = margin+cellMargin;
        }
    }



}
