package com.glface.modules.controller;

import com.alibaba.fastjson.JSONObject;
import com.glface.base.bean.DynamicBean;
import com.glface.base.bean.R;
import com.glface.base.utils.Encodes;
import com.glface.base.utils.StringUtils;
import com.glface.common.ImageCode;
import com.glface.common.utils.NewSmsUtil;
import com.glface.common.utils.SmsUtil;
import com.glface.common.web.ApiCode;
import com.glface.model.SysSms;
import com.glface.modules.mapper.SmsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/system")
public class ValidateCodeController {
    public final static int CODE_AMOUNT = 5;

    @Resource
    private SmsMapper smsMapper;

    /**
     * 发送短信验证码
     * @param mobile
     * @param imageCode
     * @param imageCodeHash
     * @param imageCodeTamp
     * @return
     */
    @RequestMapping("/code/sms")
    public R<Object> sendSmsCode(String mobile, String imageCode, String imageCodeHash, String imageCodeTamp) {

        if(StringUtils.isBlank(imageCode)||StringUtils.isBlank(imageCodeHash)||StringUtils.isBlank(imageCodeTamp)){
            return R.fail(ApiCode.IMAGE_CODE_EMPTY.getMsg());
        }
        //校验图形验证码
        String sHashImage =  Encodes.md5(imageCode + "@" + imageCodeTamp + "@"  + imageCode.length(),null);//生成MD5值
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar c1 = Calendar.getInstance();
        String cTime = sf.format(c1.getTime());
        if (imageCodeTamp.compareTo(cTime) > 0) {
            if (!sHashImage.equalsIgnoreCase(imageCodeHash)) {
                //验证码不正确，校验失败
                return R.fail(ApiCode.IMAGE_CODE_ERROR.getMsg());
            }
        } else {
            // 超时
            return R.fail(ApiCode.IMAGE_CODE_TIME_OUT.getMsg());
        }
        Random random = new Random();
        String randomNum="";
        for (int i=0;i<6;i++)
        {
            randomNum+=random.nextInt(10);
        }
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, CODE_AMOUNT);
        String currentTime = sf.format(c.getTime());// 生成5分钟后时间，用户校验是否过期
        HashMap map = new HashMap();
        JSONObject sendResult = null;
        SysSms sysSms = new SysSms();
        sysSms.setCode(randomNum);
        sysSms.setPhone(mobile);
        Date now = new Date();
        sysSms.setCreateDate(now);
        sysSms.setUpdateDate(now);
        sysSms.setCreateBy(com.glface.modules.sys.utils.UserUtils.getUserId());
        sysSms.setUserId(com.glface.modules.sys.utils.UserUtils.getUserId());
        try {
            sendResult = NewSmsUtil.sendCode(mobile, Integer.valueOf(randomNum));
            sysSms.setReturnCode(sendResult.getString("code"));
            sysSms.setReturnDescription(sendResult.getString("description"));
            sysSms.setReturnResult(sendResult.getString("result"));
        } catch (Exception e) {
            e.printStackTrace();
            sysSms.setReturnResult("发送验证码失败：" + e.getMessage());
        }
        smsMapper.insert(sysSms);
        //发送成功
        if(sendResult!=null&&sendResult.getString("code").equals("000000")){
            String hash =  Encodes.md5(mobile + "@" + currentTime + "@" + randomNum,null);//生成MD5值
            map.put("hash", hash);
            map.put("tamp", currentTime);
            Object data = new DynamicBean.Builder()
                    .setPV("hash", hash)
                    .setPV("tamp", currentTime)
                    .build().getObject();
            return R.ok(data);
        }
        return R.fail(ApiCode.CODE_SEND_FAILED.getMsg());

    }

    /**
     * 图形验证码
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/code/image")
    public void createCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        /**
         * 1.根据随机数生成图片
         * 2.将随机数存到session中
         * 3.将生成图片写到接口的响应中
         */
        ArrayList codes = getRandNumber();
        ImageCode imageCode = createImageCode(request,codes);
        String sRand = "";
        for (int i = 0; i < codes.size(); i++) {
            sRand += codes.get(i);
        }
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, 1);
        String currentTime = sf.format(c.getTime());// 生成1分钟后时间，用户校验是否过期
        String hash =  Encodes.md5(sRand + "@" + currentTime + "@"  + codes.size(),null);//生成MD5值
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-disposition", "attachment;image_code_hash="+ hash + "image_code_tamp=" + currentTime);
        response.setCharacterEncoding("UTF-8");
        ImageIO.write(imageCode.getImage(),"JPEG",response.getOutputStream());


    }

    private ImageCode createImageCode(HttpServletRequest request,ArrayList<String> codes) {
        int width = 67;
        int height = 23;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics g = image.getGraphics();

        Random random = new Random();

        g.setColor(getRandColor(200, 250));
        g.fillRect(0, 0, width, height);
        g.setFont(new Font("Times New Roman", Font.ITALIC, 20));
        g.setColor(getRandColor(160, 200));
        for (int i = 0; i < 155; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            g.drawLine(x, y, x + xl, y + yl);
        }
        String sRand = "";
        for (int i = 0; i < codes.size(); i++) {
            sRand += codes.get(i);
            g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
            g.drawString(codes.get(i), 13 * i + 6, 16);
        }

        g.dispose();
        return new ImageCode(image, sRand, 60);
    }

    public ArrayList<String> getRandNumber(){
        ArrayList<String> linkedList = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            String rand = String.valueOf(random.nextInt(10));
            linkedList.add(rand);
        }
        return linkedList;
    }
    /**
     * 生成随机背景条纹
     *
     * @param fc
     * @param bc
     * @return
     */
    private Color getRandColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }
}
