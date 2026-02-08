package com.inn.cafe.serviceImpl;

import com.inn.cafe.PROJO.Bill;
import com.inn.cafe.constents.CafeConstants;
import com.inn.cafe.dao.BillDao;
import com.inn.cafe.jwt.JwtFilter;
import com.inn.cafe.service.BillService;
import com.inn.cafe.utils.CafeUtils;
import com.itextpdf.text.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.*;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.stream.Stream;


@Slf4j
@Service
public class BillServiceImpl implements BillService {
   @Autowired
   private JwtFilter jwtFilter;
   @Autowired
   private BillDao billDao;
   @Override
   public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
     log.info("Inside generateReport");
    try{
      String fileName;
      if (validateRequestMap(requestMap)){
       if (requestMap.containsKey("isGenerate") &&!(Boolean) requestMap.get("isGenerate")){
           fileName=(String) requestMap.get("uuid");
       }
       else{
           fileName=CafeUtils.getUUID();
           requestMap.put("uuid",fileName);
           insertBill(requestMap);

       }
       String data = "Name : "+requestMap.get("name") +"\n" + "Contact Number : "+requestMap.get("contactNumber") +"\n"+ "Email : "+requestMap.get("email") +"\n"+
               "Payment Method : "+requestMap.get("paymentMethod");
          Document document = new Document();
          PdfWriter.getInstance(document,new FileOutputStream(CafeConstants.STORE_LOCATION+"\\"+fileName+".pdf"));
          document.open();
          setRectangleInPdf(document);
          Paragraph p = new Paragraph("Cafe Managment System",getFont("Header"));
          p.setAlignment(Paragraph.ALIGN_CENTER);
          document.add(p);
          Paragraph f = new Paragraph(data+"\n \n",getFont("Data"));
          document.add(f);

          PdfPTable table = new PdfPTable(5);
          table.setWidthPercentage(100);
          addTableHeader(table);

          JSONArray jsonArray = CafeUtils.getJsonArray((String) requestMap.get("productDetails"));
          for (int i = 0; i < jsonArray.length(); i++){
            addRows(table,CafeUtils.getMapFromJson(jsonArray.getString(i)));
          }
          document.add(table);
          Paragraph p1 = new Paragraph("Total : " + requestMap.get("totalAmount")
          + "\n" + "Thank you for visiting Cafe");
          document.add(p1);
          document.close();
          return new ResponseEntity<>("{\"uuid\":\""+fileName+"\"}",HttpStatus.OK);



      }
      return CafeUtils.getResponseEntity("required data not found", HttpStatus.BAD_REQUEST);

    }
    catch(Exception e){
        e.printStackTrace();
    }
    return CafeUtils.getResponseEntity(CafeConstants.SOMETHINGS_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void addRows(PdfPTable table, Map<String, Object> mapFromJson) {
        log.info("Inside addRows");
        table.addCell((String) mapFromJson.get("name"));
        table.addCell((String) mapFromJson.get("category"));
        table.addCell((String) mapFromJson.get("quantity"));
        table.addCell(Double.toString((Double) mapFromJson.get("price")));
        table.addCell(Double.toString((Double) mapFromJson.get("total")));
    }

    private void addTableHeader(PdfPTable table) {
       log.info("Inside addTableHeader");
       Stream.of("Name","Category","Quantity","Price","Sub Total")
        .forEach(columnTitle->{
            PdfPCell header = new PdfPCell();
            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header.setBorderWidth(2);
            header.setPhrase(new Phrase(columnTitle));
            header.setBackgroundColor(BaseColor.YELLOW);
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.setVerticalAlignment(Element.ALIGN_CENTER);
            table.addCell(header);

        }) ;


    }

    private void setRectangleInPdf(Document document) throws DocumentException {
        log.info("Inside setRectangleInPdf");
        Rectangle rect = new Rectangle(577,825,18,15);
        rect.enableBorderSide(1);
        rect.enableBorderSide(2);
        rect.enableBorderSide(4);
        rect.enableBorderSide(8);
        rect.setBorderColor(BaseColor.BLACK);
        rect.setBorderWidth(1);
        document.add(rect);



    }

    private void insertBill(Map<String, Object> requestMap) {
    try{
        Bill  bill = new Bill();
        bill.setUuid((String) requestMap.get("uuid"));
        bill.setName((String) requestMap.get("name"));
        bill.setEmail((String)requestMap.get("email"));
        bill.setContactNumber((String) requestMap.get("contactNumber"));
        bill.setPaymentMethode((String) requestMap.get("paymentMethod"));
        bill.setTotal(Integer.parseInt((String)  requestMap.get("totalAmount")));
        bill.setPrductDetails((String) requestMap.get("productDetails"));
        bill.setCreatedBy(jwtFilter.getCurrentUser());
        billDao.save(bill);

    }
    catch(Exception e){
        e.printStackTrace();
    }

    }

    private boolean validateRequestMap(Map<String, Object> requestMap) {
         return requestMap.containsKey("name")
                 && requestMap.containsKey("contactNumber")
                 && requestMap.containsKey("email")
                 && requestMap.containsKey("paymentMethod")
                 && requestMap.containsKey("productDetails")
                 && requestMap.containsKey("totalAmount");

    }
    private Font getFont(String header ){
       switch (header){
           case "Header":
               Font headerFont =  FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 18,BaseColor.BLACK);
               headerFont.setStyle(Font.BOLD);
               return headerFont;

           case "Data":
               Font dataFont =  FontFactory.getFont(FontFactory.TIMES_ROMAN, 11,BaseColor.BLACK);
               dataFont.setStyle(Font.BOLD);
               return dataFont;
           default:
               return new Font() ;
       }

    }
}
