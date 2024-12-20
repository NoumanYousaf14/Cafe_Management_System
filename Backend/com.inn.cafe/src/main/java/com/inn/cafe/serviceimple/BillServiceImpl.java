package com.inn.cafe.serviceimple;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inn.cafe.JWT.JwtFilter;
import com.inn.cafe.POJO.Bill;
import com.inn.cafe.constents.CafeConstants;
import com.inn.cafe.dao.BillDao;
import com.inn.cafe.service.BillService;
import com.inn.cafe.untils.CafeUtils;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.pdfbox.io.IOUtils;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;


@Service
public class BillServiceImpl implements BillService {
    private static final Logger log = LoggerFactory.getLogger(BillServiceImpl.class);

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    BillDao billDao;
    //-----------service of report generation
    @Override
    public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
        log.info("Inside report generation");

        try{
            String fileName;
            if (validateRequestMap(requestMap)){
                if (requestMap.containsKey("isGenerate") && !(Boolean)requestMap.get("isGenerate")){
                    fileName=(String) requestMap.get("uuid");

                }
                else{
                    fileName=CafeUtils.getUUID();
                    requestMap.put("uuid",fileName);
                    insertBill(requestMap);
                }
//                data of the file
                String data="Name: "+requestMap.get("name")+"\n"+"Contact Number: "+requestMap.get("contactNumber")+
                        "\n" +"Email: "+requestMap.get("email")+"\n"+"Payment Method: "+requestMap.get("paymentMethod");

//                create new document
                Document document=new Document();
                PdfWriter.getInstance(document, new FileOutputStream(CafeConstants.STORE_LOC + "/" + fileName + ".pdf"));


                document.open();
//                add border to document
                setRectangleInPdf(document);

//                add heading to the document
                Paragraph chunk=new Paragraph("Cafe Management System",getFont("Header"));
                chunk.setAlignment(Element.ALIGN_CENTER);
                document.add(chunk);

//                add paragraph or data in the pdf file
                Paragraph paragraph=new Paragraph(data+"\n\n",getFont("Data"));
                document.add(paragraph);

//                add the table header to table
                PdfPTable table=new PdfPTable(5);
                table.setWidthPercentage(100);
                addTableHeader(table);

//                add data to the table
                JSONArray jsonArray= CafeUtils.getJSONArrayFromString((String) requestMap.get("productDetails"));
                for (int i = 0; i <jsonArray.length() ; i++) {
                    addTableRows(table,CafeUtils.getMapFromJson(jsonArray.getString(i)));
                }
//                add table to document
                document.add(table);

//                add footer to the pdf file
                Paragraph footer=new Paragraph("Total: "+requestMap.get("totalAmount")+"\n"+"Thank for visiting.Please visit again!!",getFont("Data"));
                document.add(footer);

                document.close();
                return new ResponseEntity<>(new ObjectMapper().writeValueAsString(Map.of("uuid", fileName)), HttpStatus.OK);

            }
            return CafeUtils.getResponseEntity("Required data does not found",HttpStatus.BAD_REQUEST);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //    add table row in pdf file
    private void addTableRows(PdfPTable table, Map<String, Object> mapFromJson) {
        log.info("inside table rows");
        table.addCell((String) mapFromJson.get("name"));
        table.addCell((String) mapFromJson.get("category"));
        table.addCell((String) mapFromJson.get("quantity"));
        table.addCell(Double.toString((Double) mapFromJson.get("price")));
        table.addCell(Double.toString((Double) mapFromJson.get("total")));

    }

    //    add table header to the pdf
    private void addTableHeader(PdfPTable table) {
        log.info("inside the table header");
        Stream.of("Name","Category","Quantity","Price","Sub-total")
                .forEach(columnTittle->{
                    PdfPCell header=new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTittle));
                    header.setBackgroundColor(BaseColor.YELLOW);
                    header.setVerticalAlignment(Element.ALIGN_CENTER);
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(header);

                });

    }

    //    function for the font of text
    private Font getFont(String type) {
        log.info("Inside the get font");
        switch (type){
            case "Header":
                Font headerFont=FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE,18,BaseColor.BLACK);
                headerFont.setStyle(Font.BOLD);
                return headerFont;
            case "Data":
                Font dataFont=FontFactory.getFont(FontFactory.TIMES_ROMAN,11,BaseColor.BLACK);
                dataFont.setStyle(Font.BOLD);
                return dataFont;
            default:
                 return new Font();
        }

    }

    //    Function to add the border in pdf file
    private void setRectangleInPdf(Document document) throws DocumentException {
        log.info("Inside the Set border in pdf file");
        Rectangle rectangle=new Rectangle(577,825,18,15);
        rectangle.enableBorderSide(1);
        rectangle.enableBorderSide(2);
        rectangle.enableBorderSide(4);
        rectangle.enableBorderSide(8);
        rectangle.setBorderColor(BaseColor.BLACK);
        rectangle.setBorderWidth(1);
        document.add(rectangle);

    }

    //    function to validate the request map
    private boolean validateRequestMap(Map<String, Object> requestMap) {
        return requestMap.containsKey("name") &&
                requestMap.containsKey("contactNumber") &&
                requestMap.containsKey("email") &&
                requestMap.containsKey("paymentMethod")&&
                requestMap.containsKey("productDetails")&&
                requestMap.containsKey("totalAmount");

    }

    //    function to insert into Bill
    private void insertBill(Map<String, Object> requestMap) {
        try{
            Bill bill=new Bill();
            bill.setUuid(((String)requestMap.get("uuid")));
            bill.setName((String)requestMap.get("name"));
            bill.setEmail((String)requestMap.get("email"));
            bill.setContactNumber((String)requestMap.get("contactNumber"));
            bill.setPaymentMethod((String)requestMap.get("paymentMethod"));
            bill.setTotal(Integer.parseInt((String) requestMap.get("totalAmount")));
            bill.setProductDetails((String) requestMap.get("productDetails"));
            bill.setCreatedBy(jwtFilter.getCurrentUser());
            billDao.save(bill);

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }



    //-----------service of get bills
    @Override
    public ResponseEntity<List<Bill>> getBills() {
        try{
            List<Bill> list=new ArrayList<>();
            if (jwtFilter.isAdmin()){
                list=billDao.getAllBills();
            }
            else{
                list=billDao.getAllBillsByUserName(jwtFilter.getCurrentUser());
            }
            return new ResponseEntity<>(list,HttpStatus.OK);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //-----------service of get pdf of bills
    @Override
    public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {
        log.info("inside the get pdf : requestMap {}",requestMap);
        try{
            byte[] byteArray=new byte[0];
            if(!requestMap.containsKey("uuid") && validateRequestMap(requestMap)){
                return new ResponseEntity<>(byteArray,HttpStatus.BAD_REQUEST);
            }
            String filePath=CafeConstants.STORE_LOC+"/"+requestMap.get("uuid")+".pdf";

            if (CafeUtils.isFileExist(filePath)){
                byteArray=getByteArray(filePath);
                return new ResponseEntity<>(byteArray,HttpStatus.OK);
            }
            else{
                requestMap.put("isGenerate",false);
                generateReport(requestMap);
                byteArray=getByteArray(filePath);
                return new ResponseEntity<>(byteArray,HttpStatus.OK);
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new byte[0],HttpStatus.INTERNAL_SERVER_ERROR);
    }


    //    Function to get form byte Array
    private byte[] getByteArray(String filePath) throws Exception{
        File initialFile=new File(filePath);
        InputStream targetStream=new FileInputStream(initialFile);
        byte[] byteArray=  IOUtils.toByteArray(targetStream);
        targetStream.close();
        return byteArray;
    }

    //-----------service to delete bills
    @Override
    public ResponseEntity<String> deleteBill(Integer id) {
        try{
            Optional<Bill> optional=billDao.findById(id);
            if (optional.isPresent()){
                billDao.deleteById(id);
                return CafeUtils.getResponseEntity("Bill is deleted Successfully",HttpStatus.OK);
            }
            return CafeUtils.getResponseEntity("Bill id does not exist.",HttpStatus.OK);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }




}
