# Finagram API

This library give you set of scala classes that represent [Telegram Bot API](https://core.telegram.org/bots/api).

## Telegram Client

`ru.finagram.TelegramClient` is a main class for issue requests to the Telegram.
Supported requests:
- Get updates
- Get file
- Edit message reply markup
- Send answer

There are two key entities: TelegramResponses and Answers. The first is responses from Telegram that you can receive as webhook or by issue GetUpdates request.
Answers is the messages that your bot should send to telegram user.

## Telegram responses
![Class diagram](http://www.plantuml.com/plantuml/png/XLDDRyCW3BtpApXFonz0sWwhDcdIkg-kfwe7bhW9AeKCgDfzzByF22GaJTGS8jb-zhEV3HC6r8RcW3-0GwdexXrqBOK6_8km_UIMu3ifEL21JWZbd6gD7nY70Kw1tdD3VAW3pgTWImsgWqNk-sCEjM5I17G1EbVCUmXU6CL4wVsWb5GpMG31BpwwUXHctTLRrWKre0US2_XQDH4FPUteZJEWzzvuB0X-afOk6KLhMa8FksjyRHS1atGoevn_qdpxjWVrtSlDqmZf0rt-kIRPlix-RiyMXNhRc0BDBqEYQfVYLraX3o8Big6-C9bYPMLS_cWuvWOl6jCdux19AunXOfs-RCbE0q4ZTjXJkrJhsYYvQ_Vchd7jl7b5xQ7CxDzPZcbu1g-LD7B1Vk8QDrPyLpUvpWChJ4MmFKblLk0KwMoNuGfCtEx2i7mBgjV_s00J4wHDuxE9i6wgaqYJli6ZIKZF6CVeKgwKoDXQhIJ-bhlQuRhY0NHw3YC38GRHiVVbnMBjCgvH5CcrwopRBqsZK-yNt-1dAS11cE1CmBnJ8q2R8X69Jly0)

## Answers
![Class diagram](http://www.plantuml.com/plantuml/png/tPJDIiGm58NtUOeymFG52Wgw44LdBCo02-aYJMzdmiGa97UiW_hkDjFIfYt5-hCGkoda76xETsyIeH1imJZG2-LAiFILqEhZEuOtUKpljDgU5dBXM2PXfL4KWZCKMiNqtlZ_uwNM4fXAJqOBHXutUWdEiItu6etBbmeiIsRt1zEgjt3CDBDvlPoITq946sq3BzYBXzL2JDTeHJ_U0sH9jMjYuLb0EGvOLUMIEKUl90ih3d07vUVadHrXTYi_QMIBuXFakbGJE8i1p-AF0b7Q8LtZa_mdqqbs6lKqY_5o0CEPwUrO2-bNCNyuYZEwqWewY3KAlWSxZU5gmtUdyajngoD4WgSYab1Wz7RUl6makFFHmNYXCvF1BHeRWZCv5iCfDsgZz5lNqpu0)

## Types
