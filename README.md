# UAA

User authentication and authorization center

基于 spring-cloud-oauth2

## 功能

- 完善登录：账号密码模式、短信验证码模式、社交账号模式（钉钉、支付宝...）均整合Spring Security Oauth

- 单点登录：单点登录：基于Spring Security OAuth 提供单点登录接口，方便其他系统对接

## 设计思路

[关于用户系统第三方登录设计的想法](https://www.telami.cn/2018/11/07/%E7%94%A8%E6%88%B7%E7%B3%BB%E7%BB%9F%E7%AC%AC%E4%B8%89%E6%96%B9%E7%99%BB%E5%BD%95%E8%AE%BE%E8%AE%A1/)

UAA目前采用的是多种登录方式都是一个用户的设计思路