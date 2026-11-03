# 对称工艺 SymmetryCraft

**独立材质 · 独立UI · 针对本机的帧数优化** —— 一切围绕中轴对称的 Minecraft 模组。

| | |
|---|---|
| 游戏版本 | Minecraft **1.20.1** |
| 加载器 | Forge **47.4.10** |
| Java | 17 |
| 版本 | 1.0.0 |

## 内容一览

- **对称水晶 / 对称水晶块 / 对称锻炉**：完整的生存玩法链路，1 材料 + 1 水晶 = 2 份镜像成品。
- **独立 UI**：对称锻炉界面、优化设置界面（游戏内按 `O`）、顶部居中的对称性能 HUD。
- **帧数优化器**：粒子距离剔除、粒子总量上限、生物渲染距离裁剪、雾效减弱，三档预设一键切换。
- **对称美学**：全部材质由 `tools/gen_textures.py` 生成并强制通过左右镜像校验，配方图案同为对称十字与环形。

## 快速开始

```bash
gradlew build          # 产出 build/libs/symmetrycraft-1.0.0.jar
gradlew runClient      # 开发环境启动游戏
python tools/gen_textures.py   # 重新生成对称材质
```

把 `build/libs/symmetrycraft-1.0.0.jar` 放入 `.minecraft/mods/` 即可游玩。

## 文档

完整文档（安装 / 玩法 / 优化参数 / FAQ）见 `docs/index.html`，本地启动：

```bash
python -m http.server 8000
# 浏览器访问 http://localhost:8000/docs/
```

## License

MIT
