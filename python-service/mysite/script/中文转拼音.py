from pypinyin import pinyin, Style, lazy_pinyin

style = Style.TONE3
name_list = lazy_pinyin('李四', style=style)
print(''.join(name_list))