package com.xjliang.redis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class SeqController {

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping("getSeqHash/{hkey}/{hstep}")
    public String getSeqHash(HttpServletRequest request,@PathVariable String hkey,@PathVariable String hstep){
        long m = 0L;
        long lstep = Long.parseLong(hstep);
        redisTemplate.opsForHash().put("zbx_seq",hkey,m);
        redisTemplate.opsForHash().put("zbx_seq",hkey+"_step",lstep);
        return "ok";
    }
    @RequestMapping("getSeq/{hstr}")
    public long getSeq(HttpServletRequest request, @PathVariable String hstr){
        Integer lstep =(Integer) redisTemplate.opsForHash().get("zbx_seq",hstr+"_step");
        long str = redisTemplate.opsForHash().increment("zbx_seq",hstr,Long.parseLong(lstep+""));
        return str;
    }
    @RequestMapping("getRedis/{hstr}")
    public String getRedis(HttpServletRequest request, @PathVariable String hstr){
        Integer lstr =(Integer) redisTemplate.opsForHash().get("zbx_seq",hstr);
        Integer lstep =(Integer) redisTemplate.opsForHash().get("zbx_seq",hstr+"_step");
        return lstr+"---"+lstep;
    }

}
