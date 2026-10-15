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
public val reminder: ImageVector
    get() {
        if (_reminder != null) {
            return _reminder!!
        }
        _reminder =
            ImageVector.Builder(
                name = "reminder",
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
                        moveTo(6.5f, 8f)
                        horizontalLineToRelative(1f)
                        verticalLineTo(7f)
                        quadTo(7.5f, 6.57f, 7.21f, 6.29f)
                        reflectiveQuadTo(6.5f, 6f)
                        quadTo(6.08f, 6f, 5.79f, 6.29f)
                        reflectiveQuadTo(5.5f, 7f)
                        reflectiveQuadTo(5.79f, 7.71f)
                        reflectiveQuadTo(6.5f, 8f)
                        close()
                        moveTo(11f, 8f)
                        quadToRelative(0.43f, 0f, 0.71f, -0.29f)
                        quadTo(12f, 7.43f, 12f, 7f)
                        reflectiveQuadTo(11.71f, 6.29f)
                        reflectiveQuadTo(11f, 6f)
                        reflectiveQuadTo(10.29f, 6.29f)
                        reflectiveQuadTo(10f, 7f)
                        verticalLineTo(8f)
                        horizontalLineToRelative(1f)
                        close()
                        moveToRelative(1.55f, 7.5f)
                        close()
                        moveTo(10.48f, 22f)
                        quadTo(9.78f, 22f, 9.16f, 21.7f)
                        reflectiveQuadTo(8.13f, 20.85f)
                        lineTo(3.1f, 14.48f)
                        quadTo(2.9f, 14.25f, 2.93f, 13.94f)
                        reflectiveQuadTo(3.15f, 13.43f)
                        quadTo(3.65f, 12.9f, 4.35f, 12.8f)
                        reflectiveQuadToRelative(1.3f, 0.28f)
                        lineTo(7.5f, 14.2f)
                        verticalLineTo(10f)
                        horizontalLineToRelative(-1f)
                        quadTo(5.25f, 10f, 4.38f, 9.13f)
                        reflectiveQuadTo(3.5f, 7f)
                        reflectiveQuadTo(4.38f, 4.88f)
                        reflectiveQuadTo(6.5f, 4f)
                        quadTo(6.78f, 4f, 7.01f, 4.05f)
                        reflectiveQuadTo(7.5f, 4.17f)
                        verticalLineTo(3f)
                        quadTo(7.5f, 2.57f, 7.79f, 2.29f)
                        reflectiveQuadTo(8.5f, 2f)
                        quadTo(8.93f, 2f, 9.23f, 2.29f)
                        quadTo(9.53f, 2.57f, 9.53f, 3f)
                        verticalLineTo(4.4f)
                        quadTo(9.88f, 4.2f, 10.24f, 4.1f)
                        reflectiveQuadTo(11f, 4f)
                        quadToRelative(1.25f, 0f, 2.13f, 0.88f)
                        reflectiveQuadTo(14f, 7f)
                        reflectiveQuadTo(13.13f, 9.13f)
                        reflectiveQuadTo(11f, 10f)
                        horizontalLineTo(9.53f)
                        verticalLineToRelative(6f)
                        quadToRelative(0f, 0.57f, -0.51f, 0.86f)
                        reflectiveQuadTo(8f, 16.85f)
                        lineTo(7.1f, 16.3f)
                        lineToRelative(2.6f, 3.32f)
                        quadToRelative(0.15f, 0.18f, 0.35f, 0.27f)
                        reflectiveQuadTo(10.48f, 20f)
                        horizontalLineTo(16f)
                        quadToRelative(0.82f, 0f, 1.41f, -0.59f)
                        reflectiveQuadTo(18f, 18f)
                        verticalLineTo(14f)
                        quadToRelative(0f, -0.43f, -0.29f, -0.71f)
                        reflectiveQuadTo(17f, 13f)
                        horizontalLineTo(12.53f)
                        quadTo(12.1f, 13f, 11.81f, 12.71f)
                        quadTo(11.53f, 12.43f, 11.53f, 12f)
                        quadToRelative(0f, -0.43f, 0.29f, -0.71f)
                        reflectiveQuadTo(12.53f, 11f)
                        horizontalLineTo(17f)
                        quadToRelative(1.25f, 0f, 2.13f, 0.88f)
                        reflectiveQuadTo(20f, 14f)
                        verticalLineToRelative(4f)
                        quadToRelative(0f, 1.65f, -1.18f, 2.82f)
                        reflectiveQuadTo(16f, 22f)
                        horizontalLineTo(10.48f)
                        close()
                    }
                }
                .build()
        return _reminder!!
    }

private var _reminder: ImageVector? = null