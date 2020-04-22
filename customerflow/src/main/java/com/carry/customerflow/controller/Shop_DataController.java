package com.carry.customerflow.controller;

import com.carry.customerflow.bean.*;
import com.carry.customerflow.mapper.Shop_dataMapper;
import com.carry.customerflow.utils.DataUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class Shop_DataController {

    @Autowired
    private Shop_dataMapper shop_dataMapper;

    @Autowired
    private DataUtil dataUtil;

    @GetMapping("/getMainData")
    public Msg getMainData(@RequestParam("address")String address, @Param("dateTime")String dateTime){
        try{
//            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

//            System.out.println(dataUtil.getDayDiffer(Date.valueOf(dateTime),new Date(System.currentTimeMillis())));

            Integer walker_number = 0;
            Integer customer_number = 0;
            Integer new_customer = 0;
            Integer jumpout = 0;
            List<Shop_data> shop_dataList = shop_dataMapper.getMainData(address,dateTime);
            if (shop_dataList.size()==0)
                return Msg.failure("门店未添加或已经被删除").setCode(402);
            for (Shop_data shop_data:shop_dataList) {
                walker_number+=shop_data.getWalker_number();
                customer_number+=shop_data.getCustomer_number();
                new_customer+=shop_data.getNew_customer();
                jumpout+=shop_data.getJump_out();
            }
            Integer dynamicconsumer = shop_dataList.get(0).getDynamic_customer();
            Map<String,Integer> map=new HashMap<>();
            map.put("walkerNumber",walker_number);
            map.put("consumerNumber",customer_number);
            map.put("newConsumer",new_customer);
            map.put("jmpOut",jumpout);
            map.put("dynamicConsumer",dynamicconsumer);


            return Msg.success().setData(map);

        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("返回数据失败");
        }
    }

    @GetMapping("/getCustomerPerHour")
    public Msg getCustomerPerHour(@RequestParam("address")String address, @Param("dateTime")String dateTime){
        try {
            List<CustomerPerHour> customerPerHourList = shop_dataMapper.getCustomerPerHour(address, dateTime);
            Collections.reverse(customerPerHourList);
            return Msg.success().setData(customerPerHourList);
        }catch (Exception e)
        {
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("返回数据失败");
        }
    }

    @GetMapping("/getInCustomerPerHour")
    public Msg getInCustomerPerHour(@RequestParam("address")String address, @Param("dateTime")String dateTime){
        try {
            List<InCustomerPerHour> inCustomerPerHourList = shop_dataMapper.getInCustomerPerHour(address, dateTime);
            Collections.reverse(inCustomerPerHourList);
            return Msg.success().setData(inCustomerPerHourList);
        }catch (Exception e)
        {
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("返回数据失败");
        }
    }

//    ------------------------------日表

    /**
     * 返回人流量
     * @param address
     * @param dateTime
     * @return
     */
    @GetMapping("/getWalker_numberDayComparison")
    public Msg getWalker_numberDayComparison(@RequestParam("address")String address,@Param("dateTime")String dateTime){
        try{
            Integer j = 0;
            Date date = null;
            List<Comparison> comparisonList = new ArrayList<>();
            Integer walker_number = 0;
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            for (int i = 0;i<10;i++){
                walker_number = 0;
                List<Shop_data> shop_dataList = shop_dataMapper.getDayComparison(address,dateTime,i);
                for (Shop_data shopData:shop_dataList)
                    walker_number += shopData.getWalker_number();

                Comparison comparison = Comparison.builder().date(format.format(new java.util.Date(shop_dataList.get(0).getUpdate_time().getTime()))).number(walker_number).build();
                comparisonList.add(comparison);
            }
            Collections.reverse(comparisonList);
            return Msg.success(comparisonList);
        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("返回数据失败");
        }
    }

    /**
     * 返回客流量
     * @param address
     * @param dateTime
     * @return
     */
    @GetMapping("/getCustomer_numberDayComparison")
    public Msg getCustomer_numberDayComparison(@RequestParam("address")String address,@Param("dateTime")String dateTime){
        try{
            Date date = null;
            List<Comparison> comparisonList = new ArrayList<>();
            Integer customer_number = 0;
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            for (int i = 0;i<10;i++){
                customer_number = 0;
                List<Shop_data> shop_dataList = shop_dataMapper.getDayComparison(address,dateTime,i);
                for (Shop_data shopData:shop_dataList)
                    customer_number += shopData.getCustomer_number();
                Comparison comparison = Comparison.builder().date(format.format(new java.util.Date(shop_dataList.get(0).getUpdate_time().getTime()))).number(customer_number).build();
                comparisonList.add(comparison);
            }
            Collections.reverse(comparisonList);
            return Msg.success(comparisonList);
        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("返回数据失败");
        }
    }

    /**
     * 返回跳出量
     * @param address
     * @param dateTime
     * @return
     */
    @GetMapping("/getJump_outDayComparison")
    public Msg getJump_outDayComparison(@RequestParam("address")String address,@Param("dateTime")String dateTime){
        try{
            Date date = null;
            List<Comparison> comparisonList = new ArrayList<>();
            Integer jump_out = 0;
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            for (int i = 0;i<10;i++){
                jump_out = 0;
                List<Shop_data> shop_dataList = shop_dataMapper.getDayComparison(address,dateTime,i);
                for (Shop_data shopData:shop_dataList)
                    jump_out += shopData.getJump_out();
                Comparison comparison = Comparison.builder().date(format.format(new java.util.Date(shop_dataList.get(0).getUpdate_time().getTime()))).number(jump_out).build();
                comparisonList.add(comparison);
            }
            Collections.reverse(comparisonList);
            return Msg.success(comparisonList);
        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("返回数据失败");
        }
    }

    /**
     * 返回跳出量
     * @param address
     * @param dateTime
     * @return
     */
    @GetMapping("/getNew_customerDayComparison")
    public Msg getnew_customerDayComparison(@RequestParam("address")String address,@Param("dateTime")String dateTime){
        try{
            Date date = null;
            List<Comparison> comparisonList = new ArrayList<>();
            Integer new_customer = 0;
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            for (int i = 0;i<10;i++){
                new_customer = 0;
                List<Shop_data> shop_dataList = shop_dataMapper.getDayComparison(address,dateTime,i);
                for (Shop_data shopData:shop_dataList)
                    new_customer += shopData.getNew_customer();
                Comparison comparison = Comparison.builder().date(format.format(new java.util.Date(shop_dataList.get(0).getUpdate_time().getTime()))).number(new_customer).build();
                comparisonList.add(comparison);
            }
            Collections.reverse(comparisonList);
            return Msg.success(comparisonList);
        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("返回数据失败");
        }
    }

    //    ------------------------------周表


    /**
     * 人流量
     * @param address
     * @param dateTime
     * @return
     */
    @GetMapping("/getWalker_numberWeekComparison")
    public Msg getWalker_numberWeekComparison(@RequestParam("address")String address,@Param("dateTime")String dateTime) {
        try{
            Integer num = 0;
            Integer walker_number = 0;
            String formerDate;
            String latterDate;
            String Date;
            DateFormat format = new SimpleDateFormat("MM.dd");
            List<Comparison> comparisonList = new ArrayList<>();

            List<Shop_data> thisWeekShop_dataList = shop_dataMapper.getThisWeekComparison(address,dateTime);
//            System.out.println(thisWeekShop_dataList);
            for (Shop_data shopData:thisWeekShop_dataList)
                walker_number +=shopData.getWalker_number();

            formerDate = format.format(new java.util.Date(thisWeekShop_dataList.get(0).getUpdate_time().getTime()));
            latterDate = format.format(new java.util.Date(thisWeekShop_dataList.get(thisWeekShop_dataList.size()-1).getUpdate_time().getTime()));
            Date = formerDate + "-" +latterDate;

            Comparison comparison = Comparison.builder().date(Date).number(walker_number).build();
            comparisonList.add(comparison);

            for (int i=0;i<10;i++){
                walker_number = 0;
                thisWeekShop_dataList = shop_dataMapper.getWeekComparison(address,dateTime,num);
                if (thisWeekShop_dataList.size()==0)
                    break;
                for (Shop_data shopData:thisWeekShop_dataList)
                    walker_number +=shopData.getWalker_number();

                formerDate = format.format(new java.util.Date(thisWeekShop_dataList.get(0).getUpdate_time().getTime()));
                latterDate = format.format(new java.util.Date(thisWeekShop_dataList.get(thisWeekShop_dataList.size()-1).getUpdate_time().getTime()));
                Date = formerDate + "-" +latterDate;

                comparison = Comparison.builder().date(Date).number(walker_number).build();
                comparisonList.add(comparison);

                num += 7;
            }

            Collections.reverse(comparisonList);
            return Msg.success(comparisonList);

        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("返回数据失败");
        }
    }

    /**
     * 客流量
     * @param address
     * @param dateTime
     * @return
     */
    @GetMapping("/getCustomer_numberWeekComparison")
    public Msg getCustomer_numberWeekComparison(@RequestParam("address")String address,@Param("dateTime")String dateTime) {
        try{
            Integer num = 0;
            Integer customer_number = 0;
            String formerDate;
            String latterDate;
            String Date;
            DateFormat format = new SimpleDateFormat("MM.dd");
            List<Comparison> comparisonList = new ArrayList<>();

            List<Shop_data> thisWeekShop_dataList = shop_dataMapper.getThisWeekComparison(address,dateTime);
//            System.out.println(thisWeekShop_dataList);
            for (Shop_data shopData:thisWeekShop_dataList)
                customer_number +=shopData.getCustomer_number();

            formerDate = format.format(new java.util.Date(thisWeekShop_dataList.get(0).getUpdate_time().getTime()));
            latterDate = format.format(new java.util.Date(thisWeekShop_dataList.get(thisWeekShop_dataList.size()-1).getUpdate_time().getTime()));
            Date = formerDate + "-" +latterDate;

            Comparison comparison = Comparison.builder().date(Date).number(customer_number).build();
            comparisonList.add(comparison);

            for (int i=0;i<10;i++){
                customer_number = 0;
                thisWeekShop_dataList = shop_dataMapper.getWeekComparison(address,dateTime,num);
                if (thisWeekShop_dataList.size()==0)
                    break;
                for (Shop_data shopData:thisWeekShop_dataList)
                    customer_number +=shopData.getCustomer_number();

                formerDate = format.format(new java.util.Date(thisWeekShop_dataList.get(0).getUpdate_time().getTime()));
                latterDate = format.format(new java.util.Date(thisWeekShop_dataList.get(thisWeekShop_dataList.size()-1).getUpdate_time().getTime()));
                Date = formerDate + "-" +latterDate;

                comparison = Comparison.builder().date(Date).number(customer_number).build();
                comparisonList.add(comparison);

                num += 7;
            }

            Collections.reverse(comparisonList);
            return Msg.success(comparisonList);

        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("返回数据失败");
        }
    }

    /**
     * 跳出量
     * @param address
     * @param dateTime
     * @return
     */
    @GetMapping("/getJump_outWeekComparison")
    public Msg getJump_outWeekComparison(@RequestParam("address")String address,@Param("dateTime")String dateTime) {
        try{
            Integer num = 0;
            Integer jump_out = 0;
            String formerDate;
            String latterDate;
            String Date;
            DateFormat format = new SimpleDateFormat("MM.dd");
            List<Comparison> comparisonList = new ArrayList<>();

            List<Shop_data> thisWeekShop_dataList = shop_dataMapper.getThisWeekComparison(address,dateTime);
//            System.out.println(thisWeekShop_dataList);
            for (Shop_data shopData:thisWeekShop_dataList)
                jump_out +=shopData.getJump_out();

            formerDate = format.format(new java.util.Date(thisWeekShop_dataList.get(0).getUpdate_time().getTime()));
            latterDate = format.format(new java.util.Date(thisWeekShop_dataList.get(thisWeekShop_dataList.size()-1).getUpdate_time().getTime()));
            Date = formerDate + "-" +latterDate;

            Comparison comparison = Comparison.builder().date(Date).number(jump_out).build();
            comparisonList.add(comparison);

            for (int i=0;i<10;i++){
                jump_out = 0;
                thisWeekShop_dataList = shop_dataMapper.getWeekComparison(address,dateTime,num);
                if (thisWeekShop_dataList.size()==0)
                    break;
                for (Shop_data shopData:thisWeekShop_dataList)
                    jump_out +=shopData.getJump_out();

                formerDate = format.format(new java.util.Date(thisWeekShop_dataList.get(0).getUpdate_time().getTime()));
                latterDate = format.format(new java.util.Date(thisWeekShop_dataList.get(thisWeekShop_dataList.size()-1).getUpdate_time().getTime()));
                Date = formerDate + "-" +latterDate;

                comparison = Comparison.builder().date(Date).number(jump_out).build();
                comparisonList.add(comparison);

                num += 7;
            }

            Collections.reverse(comparisonList);
            return Msg.success(comparisonList);

        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("返回数据失败");
        }
    }

    /**
     * 新客人量
     * @param address
     * @param dateTime
     * @return
     */
    @GetMapping("/getNew_customerWeekComparison")
    public Msg getNew_customerWeekComparison(@RequestParam("address")String address,@Param("dateTime")String dateTime) {
        try{
            Integer num = 0;
            Integer new_Customer = 0;
            String formerDate;
            String latterDate;
            String Date;
            DateFormat format = new SimpleDateFormat("MM.dd");
            List<Comparison> comparisonList = new ArrayList<>();

            List<Shop_data> thisWeekShop_dataList = shop_dataMapper.getThisWeekComparison(address,dateTime);
//            System.out.println(thisWeekShop_dataList);
            for (Shop_data shopData:thisWeekShop_dataList)
                new_Customer +=shopData.getNew_customer();

            formerDate = format.format(new java.util.Date(thisWeekShop_dataList.get(0).getUpdate_time().getTime()));
            latterDate = format.format(new java.util.Date(thisWeekShop_dataList.get(thisWeekShop_dataList.size()-1).getUpdate_time().getTime()));
            Date = formerDate + "-" +latterDate;

            Comparison comparison = Comparison.builder().date(Date).number(new_Customer).build();
            comparisonList.add(comparison);

            for (int i=0;i<10;i++){
                new_Customer = 0;
                thisWeekShop_dataList = shop_dataMapper.getWeekComparison(address,dateTime,num);
                if (thisWeekShop_dataList.size()==0)
                    break;
                for (Shop_data shopData:thisWeekShop_dataList)
                    new_Customer +=shopData.getNew_customer();

                formerDate = format.format(new java.util.Date(thisWeekShop_dataList.get(0).getUpdate_time().getTime()));
                latterDate = format.format(new java.util.Date(thisWeekShop_dataList.get(thisWeekShop_dataList.size()-1).getUpdate_time().getTime()));
                Date = formerDate + "-" +latterDate;

                comparison = Comparison.builder().date(Date).number(new_Customer).build();
                comparisonList.add(comparison);

                num += 7;
            }

            Collections.reverse(comparisonList);
            return Msg.success(comparisonList);

        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("返回数据失败");
        }
    }
    }
