package co.shop.luxury.serviceImpl;

import co.shop.luxury.JsonWebToken.JwtFilter;
import co.shop.luxury.constant.JoyeriaConstant;
import co.shop.luxury.model.Order;
import co.shop.luxury.repository.OrderRepository;
import co.shop.luxury.service.OrderService;
import co.shop.luxury.utils.JoyeriaUtils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> generate(Map<String, Object> requestMap) {
        log.info("Inside generate ");
        try{
            String fileName;
            if(validateRequestMap(requestMap)){
                if(requestMap.containsKey("isGenerate") && !(Boolean) requestMap.get("isGenerate")){
                    fileName = (String) requestMap.get("uuid");
                }else{
                    fileName = JoyeriaUtils.getUUID();
                    requestMap.put("uuid", fileName);
                    insertOrder(requestMap);
                }

                String data = "Nombre: "+requestMap.get("name")+"\n"+"Número de contacto: "+requestMap.get("contactNumber")+
                        "\n"+"Correo electrónico: "+requestMap.get("email")+"\n"+"Método de pago: "+requestMap.get("payMethod");
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(JoyeriaConstant.STORE_LOCATION+"\\"+fileName+".pdf"));
                
                document.open();
                setRectangleInPdf(document);

                Paragraph chunk = new Paragraph("Joyeria Luxury Shop", getFont("Header"));
                chunk.setAlignment(Element.ALIGN_CENTER);
                document.add(chunk);

                Paragraph paragraph = new Paragraph(data+"\n \n", getFont("Data"));
                document.add(paragraph);

                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                addTableHeader(table);

                JSONArray jsonArray = JoyeriaUtils.getJsonFromString((String) requestMap.get("productDetails"));
                for(int i =0; i<jsonArray.length(); i++){
                    addRow(table, JoyeriaUtils.getMapFromJson(jsonArray.getString(i)));
                }
                document.add(table);

                Paragraph footer = new Paragraph("Total = "+requestMap.get("totalAmount")+"\n\n"+
                        "Gracias por su pedido. Estamos felices de haberte servido", getFont("Data"));

                document.add(footer);
                document.close();
                return new ResponseEntity<>("{\"orden\":\""+fileName+"\"}", HttpStatus.OK);

            }else{
                return JoyeriaUtils.getResponseEntity("No contiene toda la información requerida", HttpStatus.BAD_REQUEST);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return JoyeriaUtils.getResponseEntity(JoyeriaConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<Order>> getOrders() {
        List<Order> list =  new ArrayList<>();
        if(jwtFilter.isAdmin()){
            list = orderRepository.getAllOrders();
        }else{
            list = orderRepository.getOrderByUsername(jwtFilter.getCurrentUser());
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    private void addRow(PdfPTable table, Map<String, Object> data) {
        log.info("Inside addRow");
        table.addCell((String) data.get("name"));
        table.addCell((String) data.get("category"));
        table.addCell((String) data.get("quantity"));
        table.addCell(Double.toString((Double) data.get("price")));
        table.addCell(Double.toString((Double) data.get("total")));

    }

    private void addTableHeader(PdfPTable table) {
        log.info("Inside table header");
        Stream.of("Nombre", "Categoria", "Cantidad", "Precio", "Sub-Total")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    header.setBackgroundColor(BaseColor.BLUE);
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setVerticalAlignment(Element.ALIGN_CENTER);
                    table.addCell(header);

                }  );

    }

    private Font getFont(String type) {
        log.info("Inside getFont");
        switch(type){
            case "Header":
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 18, BaseColor.BLACK);
                headerFont.setStyle(Font.BOLD);
                return headerFont;
            case "Data":
                Font dataFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, BaseColor.BLACK);
                dataFont.setStyle(Font.BOLD);
                return dataFont;
            default:
                return new Font();
        }
    }

    private void setRectangleInPdf(Document document) throws DocumentException {
        log.info("Inside setRectangle");
        Rectangle rect = new Rectangle(577,825, 18, 15);
        rect.enableBorderSide(1);
        rect.enableBorderSide(2);
        rect.enableBorderSide(4);
        rect.enableBorderSide(8);
        rect.setBorderColor(BaseColor.BLACK);
        rect.setBorderWidth(1);
        document.add(rect);
    }

    private void insertOrder(Map<String, Object> requestMap) {
        try{
            Order order = new Order();
            order.setUuid((String)requestMap.get("uuid"));
            order.setName((String)requestMap.get("name"));
            order.setEmail((String)requestMap.get("email"));
            order.setContactNumber((String)requestMap.get("contactNumber"));
            order.setPayMethod((String)requestMap.get("payMethod"));
            order.setTotal((Integer)requestMap.get("total"));
            order.setProductDetails((String)requestMap.get("productDetails"));
            order.setCreatedBy(jwtFilter.getCurrentUser());
            orderRepository.save(order);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private boolean validateRequestMap(Map<String, Object> requestMap) {
        return requestMap.containsKey("name") && requestMap.containsKey("contactNumber") &&
                requestMap.containsKey("email") && requestMap.containsKey("payMethod") &&
                requestMap.containsKey("productDetails") && requestMap.containsKey("total");
    }
}
