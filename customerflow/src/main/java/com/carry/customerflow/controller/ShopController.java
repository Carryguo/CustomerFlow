package com.carry.customerflow.controller;

import com.carry.customerflow.bean.Msg;
import com.carry.customerflow.bean.Shop;
import com.carry.customerflow.bean.User;
import com.carry.customerflow.mapper.Shop_dataMapper;
import com.carry.customerflow.service.MachineService;
import com.carry.customerflow.service.ShopService;
import com.carry.customerflow.utils.DataUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ShopController
{
    @Autowired
    private ShopService shopService;

    @Autowired
    private Shop_dataMapper shop_dataMapper;

    @Autowired
    private MachineService machineService;

    @Autowired
    private DataUtil dataUtil;


    /**
     * 不要下权限，只认证登录
     * 店员也能用
     * @param username
     * @return
     */
    @GetMapping("/findShopByBName")
    public Msg findShopByBossName(@RequestParam("username")String username){
        return findShop(username);
    }

    /**
     * 修改绑定的店铺
     * @param username
     * @param address
     * @return
     */
    @PostMapping("/changeBondShop")
    public Msg changeBondShop(@RequestParam("username")String username,@RequestParam("address")String address){
        try{
            shopService.changeBondShop(username,address);
            return Msg.success().setMessage("绑定成功");
        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("服务器错误");
        }
    }

    /**
     * 店员权限专用
     * @param username
     * @return
     */
    @GetMapping("/findShopByBNameOnlyStaff")
    public Msg findShopByBNameOnlyStaff(@RequestParam("username")String username){
        return findShop(username);
    }

    /**
     * 根据店主的名字返回店铺
     * @return
     */
    @GetMapping("/findShop")
    public Msg findShopByUsername(@RequestParam("username")String username){
        //这个地方到时候要从Session中获取用户名放进去
//        User user = (User)SecurityUtils.getSubject().getPrincipal();
        return findShop(username);
    }

    /**
     * 根据店主的名字返回店铺 用于设计权限检查
     * @return
     */
    @GetMapping("/findShopByUsernameCheckPermission")
    public Msg findShopByUsernameCheckPermission(@RequestParam("username")String username){
        //这个地方到时候要从Session中获取用户名放进去
//        User user = (User)SecurityUtils.getSubject().getPrincipal();
        return findShop(username);
    }

    private Msg findShop(String username){
        try{
            List<Shop> shopList = shopService.findShopByUsername(username);
            if (shopList.size()==0)
                return Msg.failure().setCode(402).setMessage("店主没有添加店铺，请到地图界面添加店铺");
            return Msg.success(shopList).setMessage("返回店铺成功");
        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("返回商店失败");
        }
    }


    /**
     * 检查店铺是否存在
     * @param address
     * @return
     */
    @GetMapping("/checkShop")
    public Msg checkShop(@RequestParam("address")String address){
        try{
            if (shopService.checkShop(address)==0)
                return Msg.failure().setCode(402).setMessage("绑定的"+address+"店铺已经被删除，请重新绑定");
            return Msg.success();
        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("服务器错误");
        }
    }

    /**
     * 添加店铺
     * @param username
     * @param longitude
     * @param latitude
     * @param address
     * @return
     */
    @PostMapping("/insertShop")
    public Msg insertShop(@RequestParam("username")String username,@RequestParam("longitude")String longitude,@RequestParam("latitude")String latitude,@RequestParam("address")String address)
    {
        //这个地方到时候要从Session中获取用户名放进去
        try{
//            User user = (User)SecurityUtils.getSubject().getPrincipal();
            shopService.insertShop(username,longitude,latitude,address);

            return Msg.success().setMessage("添加店铺成功");
        }catch (DuplicateKeyException e)
        {
            return Msg.failure().setCode(401).setMessage("此处店铺已被添加,请不要重复添加");
        }catch (Exception e)
        {
            e.printStackTrace();
            return Msg.failure().setCode(402).setMessage("添加店铺异常");
        }
    }

    /**
     * 根据店名删除门店
     * @param address
     * @return
     */
    @DeleteMapping("/delectShop")
    public Msg delectShop(@RequestParam("address")String address){
        try{
            shopService.deleteShop(address);

            //删除设备
            machineService.deleteMachineByAddress(address);

            //初始化设备缓存
            dataUtil.refreshMachineCache();
        }catch (Exception e)
        {
            return Msg.failure().setCode(401).setMessage("删除失败");
        }
        return Msg.success().setMessage("删除成功");
    }

    /**
     * 查找当前在店客流量
     * @param address
     * @return
     */
    @GetMapping("/showDynamicCustomer")
    public Msg showDynamicCustomer(@RequestParam("address")String address)
    {
        try
        {
            return Msg.success(shop_dataMapper.showDynamicCustomer(address));
        }catch (Exception e)
        {
            return Msg.failure().setCode(401).setMessage("返回数据失败");
        }
    }

    /**
     * 根据店名模糊查询
     * @param address
     * @return
     */
    @GetMapping("/searchShopByAddress")
    public Msg searchShopByAddress(@RequestParam("address")String address){
        try
        {
            return Msg.success(shopService.searchShopByAddress(address));
        }catch (Exception e)
        {
            return Msg.failure().setCode(401).setMessage("服务器错误");
        }
    }
}
