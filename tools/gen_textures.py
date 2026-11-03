#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
对称工艺 SymmetryCraft · 对称材质生成器

程序化生成全部模组材质，并强制校验「左右镜像对称」：
每一行像素的左半部分必须与右半部分镜像一致，校验失败立即报错。
所有 16x16 材质同时满足垂直镜像（上下对称）。

生成内容：
  - 方块材质：对称水晶块 / 对称锻炉（顶·侧·底）
  - 物品材质：对称水晶
  - GUI 材质：对称锻炉界面（176x166 面板，256x256 画布）
  - 模组图标：icon.png（128x128，四重对称曼陀罗）
"""

import struct
import zlib
from pathlib import Path

# 项目根目录 = tools/ 的上一级；输出一律限制在材质目录内
ROOT = Path(__file__).resolve().parents[1]
ASSETS = (ROOT / "src" / "main" / "resources" / "assets" / "symmetrycraft").resolve()


def safe_asset_path(relative_name: str) -> Path:
    """把相对文件名限制在材质目录内，防止路径逃逸。"""
    candidate = (ASSETS / relative_name).resolve()
    if candidate.parent != ASSETS and ASSETS not in candidate.parents:
        raise SystemExit(f"非法输出路径：{relative_name}")
    return candidate


# ---------------------------------------------------------------- PNG 输出

def save_png(path: Path, width: int, height: int, pixel_fn):
    """pixel_fn(x, y) -> (r, g, b, a)"""
    raw = bytearray()
    for y in range(height):
        raw.append(0)  # filter: none
        for x in range(width):
            r, g, b, a = pixel_fn(x, y)
            raw += bytes((r, g, b, a))

    def chunk(tag, data):
        return (struct.pack(">I", len(data)) + tag + data
                + struct.pack(">I", zlib.crc32(tag + data) & 0xFFFFFFFF))

    png = b"\x89PNG\r\n\x1a\n"
    png += chunk(b"IHDR", struct.pack(">IIBBBBB", width, height, 8, 6, 0, 0, 0))
    png += chunk(b"IDAT", zlib.compress(bytes(raw), 9))
    png += chunk(b"IEND", b"")

    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_bytes(png)
    print(f"  写出 {path.name}  ({width}x{height})")


def mirror_ok(width, height, pixel_fn, vertical=True, region_w=None, region_h=None):
    """在指定区域内校验水平镜像（左右对称）；vertical=True 时同时校验上下对称。
    区域外的像素不参与校验（例如 256x256 画布上只使用左上 176x166 的 GUI 面板）。"""
    rw = region_w if region_w is not None else width
    rh = region_h if region_h is not None else height
    for y in range(rh):
        for x in range(rw):
            if pixel_fn(x, y) != pixel_fn(rw - 1 - x, y):
                return False
    if vertical:
        for y in range(rh):
            for x in range(rw):
                if pixel_fn(x, y) != pixel_fn(x, rh - 1 - y):
                    return False
    return True


def build(name, width, height, pixel_fn, vertical=True, region_w=None, region_h=None):
    if not mirror_ok(width, height, pixel_fn, vertical, region_w, region_h):
        raise SystemExit(f"对称校验失败：{name} 不是镜像对称的！")
    save_png(safe_asset_path(name), width, height, pixel_fn)


def mix(c1, c2, t):
    return tuple(round(a + (b - a) * t) for a, b in zip(c1, c2))


# ---------------------------------------------------------------- 调色板

def hexc(s, a=255):
    return (int(s[0:2], 16), int(s[2:4], 16), int(s[4:6], 16), a)

EDGE = hexc("12172A")      # 最深描边
SLATE = hexc("1E2438")     # 深板岩底
SLATE_LIGHT = hexc("262E48")
CRYSTAL = hexc("6FE3F2")   # 主水晶青
CRYSTAL_MID = hexc("3FA9C9")
CRYSTAL_DEEP = hexc("2A5F8A")
GLOW_WHITE = hexc("E8FBFF")
PURPLE = hexc("7B5CFF")
STEEL = hexc("5A6272")
STEEL_DARK = hexc("3A4150")
PANEL = hexc("232837")
VENT = hexc("454E60")
STEEL_LIGHT = hexc("7A8496")
STEEL_PLATE = hexc("4A5262")
SEAM = hexc("333B4B")


# ---------------------------------------------------------------- 16x16 材质

def crystal_block(x, y):
    """对称水晶块：对称棋盘底纹 + 四角水晶 + 中心辉光。全部以到中轴距离定义，天然对称。"""
    # 边框
    if x in (0, 15) or y in (0, 15):
        return EDGE
    # 对称棋盘：用「到中轴的距离」分带（min(x,15-x)），保证左右/上下镜像不变
    band_x = min(x, 15 - x) // 4
    band_y = min(y, 15 - y) // 4
    quadrant = (band_x + band_y) % 2
    color = SLATE if quadrant == 0 else SLATE_LIGHT

    # 四角水晶菱形
    for cx, cy in ((3.5, 3.5), (11.5, 3.5), (3.5, 11.5), (11.5, 11.5)):
        d = abs(x - cx) + abs(y - cy)
        if d <= 0.5:
            color = GLOW_WHITE
        elif d <= 1.5:
            color = CRYSTAL
        elif d <= 2.5:
            color = CRYSTAL_MID

    # 中心辉光菱形
    d = abs(x - 7.5) + abs(y - 7.5)
    if d <= 0.5:
        color = GLOW_WHITE
    elif d <= 1.5:
        color = CRYSTAL

    # 细分隔线（十字沟槽，深色）
    if x in (7, 8) and color not in (GLOW_WHITE, CRYSTAL):
        color = mix(color, EDGE, 0.35)
    if y in (7, 8) and color not in (GLOW_WHITE, CRYSTAL):
        color = mix(color, EDGE, 0.35)
    return color


def forge_side(x, y):
    """对称锻炉 · 侧面：钢制边框 + 对称散热格栅 + 中心水晶核。"""
    if x in (0, 15) or y in (0, 15):
        return STEEL
    if x in (1, 14) or y in (1, 14):
        return STEEL_DARK
    # 内板
    color = PANEL
    # 对称散热格栅（左右各两组）
    if y in (4, 5, 10, 11) and (3 <= x <= 6 or 9 <= x <= 12):
        color = VENT
    # 中轴暗槽
    if x in (7, 8):
        color = hexc("1B202D")
    # 中心水晶核
    d = abs(x - 7.5) + abs(y - 7.5)
    if d <= 0.5:
        color = GLOW_WHITE
    elif d <= 1.5:
        color = CRYSTAL
    elif d <= 2.5:
        color = CRYSTAL_MID
    return color


def forge_top(x, y):
    """对称锻炉 · 顶面：钢框 + 同心菱形阵。"""
    if x in (0, 15) or y in (0, 15):
        return STEEL
    if x in (1, 14) or y in (1, 14):
        return STEEL_DARK
    color = hexc("2C3242")
    d = abs(x - 7.5) + abs(y - 7.5)
    if d <= 0.5:
        color = GLOW_WHITE
    elif d <= 2.5:
        color = CRYSTAL
    elif d <= 4.5:
        color = CRYSTAL_MID
    elif d <= 5.5:
        color = CRYSTAL_DEEP
    # 四角铆钉
    if (x, y) in ((3, 3), (12, 3), (3, 12), (12, 12)):
        color = STEEL_LIGHT
    return color


def forge_bottom(x, y):
    """对称锻炉 · 底面：钢板 + 对称铆钉。"""
    if x in (0, 15) or y in (0, 15):
        return STEEL
    if x in (1, 14) or y in (1, 14):
        return STEEL_DARK
    color = STEEL_PLATE
    if x in (7, 8) or y in (7, 8):
        color = SEAM
    if (x, y) in ((3, 3), (12, 3), (3, 12), (12, 12)):
        color = STEEL_LIGHT
    return color


def crystal_item(x, y):
    """对称水晶（物品）：八面菱晶，径向色带 + 上下高光带。"""
    d = abs(x - 7.5) + abs(y - 7.5)
    if d > 6.5:
        return (0, 0, 0, 0)
    if d > 5.5:
        color = CRYSTAL_DEEP
    elif d > 4.0:
        color = CRYSTAL_MID
    elif d > 2.0:
        color = CRYSTAL
    else:
        color = GLOW_WHITE
    # 上下镜像对称的高光带（参数必须一致）
    if y in (3, 12) and d <= 4.5:
        color = mix(color, GLOW_WHITE, 0.4)
    return color


# ---------------------------------------------------------------- GUI 176x166

GUI_W, GUI_H = 256, 256
PANEL_W, PANEL_H = 176, 166

GUI_BG = hexc("00000000")
GUI_PANEL = hexc("262B36")
GUI_EDGE_DARK = hexc("10141F")
GUI_ACCENT_CYAN = hexc("3FCFFF")
GUI_ACCENT_PURPLE = hexc("7B5CFF")
GUI_WELL = hexc("161A24")
GUI_WELL_BORDER = hexc("4A5266")
GUI_WELL_SHADOW = hexc("0D1017")
GUI_WELL_LIGHT = hexc("39404F")
GUI_DIVIDER = hexc("39414F")

FORGE_CELLS = [(61, 46), (79, 46), (97, 46)]  # 槽位格左上角（18x18）
INV_CELLS = [(7 + 18 * c, 83 + 18 * r) for r in range(3) for c in range(9)]
INV_CELLS += [(7 + 18 * c, 140) for c in range(9)]


def gui_forge(x, y):
    if x >= PANEL_W or y >= PANEL_H:
        return GUI_BG
    # 外描边 + 面板底
    if x == 0 or y == 0 or x == PANEL_W - 1 or y == PANEL_H - 1:
        return GUI_EDGE_DARK
    # 内侧对称描边：上下青、左右紫
    if y == 1 or y == PANEL_H - 2:
        return GUI_ACCENT_CYAN
    if x == 1 or x == PANEL_W - 2:
        return GUI_ACCENT_PURPLE
    color = GUI_PANEL

    # 四角 3x3 收口（左右对称）
    corners = [(2, 2), (PANEL_W - 5, 2), (2, PANEL_H - 5), (PANEL_W - 5, PANEL_H - 5)]
    for cx, cy in corners:
        if cx <= x <= cx + 2 and cy <= y <= cy + 2:
            return GUI_ACCENT_CYAN

    # 标题下分隔线（8 ↔ 167 关于中轴镜像）+ 中轴菱形
    if y in (21, 22) and x in (87, 88):
        color = GUI_ACCENT_CYAN
    elif y == 22 and 8 <= x <= PANEL_W - 9:
        color = GUI_DIVIDER

    # 锻炉槽位（中槽青色描边，两侧灰描边 —— ABA 对称）
    for i, (cx, cy) in enumerate(FORGE_CELLS):
        if cx <= x < cx + 18 and cy <= y < cy + 18:
            border = GUI_ACCENT_CYAN if i == 1 else GUI_WELL_BORDER
            return well_pixel(x - cx, y - cy, border)

    # 玩家背包槽位
    for cx, cy in INV_CELLS:
        if cx <= x < cx + 18 and cy <= y < cy + 18:
            return well_pixel(x - cx, y - cy, GUI_WELL_BORDER)

    return color


def well_pixel(ix, iy, border):
    """18x18 槽位格：1px 描边 + 四边同色的内圈（镜像对称的凹陷感）。"""
    if ix == 0 or iy == 0 or ix == 17 or iy == 17:
        return border
    if ix in (1, 16) or iy in (1, 16):
        return GUI_WELL_SHADOW
    return GUI_WELL


# ---------------------------------------------------------------- 图标 128x128

ICON = 128


def icon(x, y):
    """四重对称曼陀罗徽标：深底 + 同心菱晶 + 中轴十字。"""
    margin = 6
    if x < margin or y < margin or x >= ICON - margin or y >= ICON - margin:
        return hexc("0B0F1A")
    # 外框
    if x < margin + 3 or y < margin + 3 or x >= ICON - margin - 3 or y >= ICON - margin - 3:
        return hexc("10141F")

    cx = cy = (ICON - 1) / 2
    d = abs(x - cx) + abs(y - cy)
    if d <= 8:
        return GLOW_WHITE
    if d <= 26:
        return CRYSTAL
    if d <= 44:
        return CRYSTAL_MID
    if d <= 47:
        return PURPLE
    if d <= 52:
        return CRYSTAL_DEEP
    # 中轴十字（菱形范围内）
    on_axis = abs(x - cx) < 1.5 or abs(y - cy) < 1.5
    if on_axis and d <= 60:
        return mix(PURPLE, CRYSTAL, 0.4)
    # 底色 + 微弱径向渐变
    return mix(hexc("141B2E"), hexc("1E2438"), max(0.0, 1.0 - d / 90.0))


# ---------------------------------------------------------------- 主流程

def main():
    print("生成对称材质 …")
    build("textures/block/symmetry_crystal_block.png", 16, 16, crystal_block)
    build("textures/block/symmetry_forge_side.png", 16, 16, forge_side)
    build("textures/block/symmetry_forge_top.png", 16, 16, forge_top)
    build("textures/block/symmetry_forge_bottom.png", 16, 16, forge_bottom)
    build("textures/item/symmetry_crystal.png", 16, 16, crystal_item)
    build("textures/gui/symmetry_forge.png", GUI_W, GUI_H, gui_forge, vertical=False,
          region_w=PANEL_W, region_h=PANEL_H)
    build("icon.png", ICON, ICON, icon)
    print("完成：所有材质均通过镜像对称校验 ✓")


if __name__ == "__main__":
    main()
