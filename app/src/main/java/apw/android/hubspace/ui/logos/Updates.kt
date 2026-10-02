package apw.android.hubspace.ui.logos

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("CheckReturnValue")
public val dynamic_feed: ImageVector
    get() {
        if (_dynamic_feed != null) {
            return _dynamic_feed!!
        }
        _dynamic_feed =
            ImageVector.Builder(
                name = "dynamic_feed",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f,
            )
                .apply {
                    path(
                        fill = SolidColor(Color.Black),
                        fillAlpha = 1f,
                        stroke = null,
                        strokeAlpha = 1f,
                        strokeLineWidth = 1f,
                        strokeLineCap = StrokeCap.Butt,
                        strokeLineJoin = StrokeJoin.Bevel,
                        strokeLineMiter = 1f,
                        pathFillType = PathFillType.Companion.NonZero,
                    ) {
                        moveTo(4f, 21f)
                        quadTo(3.18f, 21f, 2.59f, 20.41f)
                        reflectiveQuadTo(2f, 19f)
                        verticalLineTo(12f)
                        horizontalLineTo(4f)
                        verticalLineToRelative(7f)
                        horizontalLineToRelative(9f)
                        verticalLineToRelative(2f)
                        horizontalLineTo(4f)
                        close()
                        moveTo(8f, 17f)
                        quadTo(7.18f, 17f, 6.59f, 16.41f)
                        reflectiveQuadTo(6f, 15f)
                        verticalLineTo(8f)
                        horizontalLineTo(8f)
                        verticalLineToRelative(7f)
                        horizontalLineToRelative(9f)
                        verticalLineToRelative(2f)
                        horizontalLineTo(8f)
                        close()
                        moveToRelative(4f, -4f)
                        quadToRelative(-0.82f, 0f, -1.41f, -0.59f)
                        reflectiveQuadTo(10f, 11f)
                        verticalLineTo(5f)
                        quadTo(10f, 4.17f, 10.59f, 3.59f)
                        reflectiveQuadTo(12f, 3f)
                        horizontalLineToRelative(8f)
                        quadToRelative(0.83f, 0f, 1.41f, 0.59f)
                        reflectiveQuadTo(22f, 5f)
                        verticalLineToRelative(6f)
                        quadToRelative(0f, 0.82f, -0.59f, 1.41f)
                        reflectiveQuadTo(20f, 13f)
                        horizontalLineTo(12f)
                        close()
                        moveToRelative(0f, -2f)
                        horizontalLineToRelative(8f)
                        verticalLineTo(7f)
                        horizontalLineTo(12f)
                        verticalLineToRelative(4f)
                        close()
                    }
                }
                .build()
        return _dynamic_feed!!
    }

private var _dynamic_feed: ImageVector? = null