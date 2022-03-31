package ru.babushkinanatoly.feature_auth

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AuthHeader(text: String) {
    Text(
        modifier = Modifier.padding(vertical = 24.dp),
        fontWeight = FontWeight.Bold,
        text = text
    )
}

@Composable
fun AuthFooter(
    modifier: Modifier = Modifier,
    text: String,
    clickableText: String,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            fontSize = 14.sp,
            text = text
        )
        Text(
            modifier = Modifier.clickable { onClick() },
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colors.primary,
            text = clickableText
        )
    }
}

@Composable
fun EmailField(
    text: String,
    error: String,
    isError: Boolean,
    onValueChange: (String) -> Unit,
) {
    CommonField(
        text = text,
        leadingImage = Icons.Default.Email,
        placeholder = stringResource(R.string.email),
        error = error,
        isError = isError,
        onValueChange = onValueChange
    )
}

@Composable
fun NameField(
    text: String,
    error: String,
    isError: Boolean,
    onValueChange: (String) -> Unit,
) {
    CommonField(
        text = text,
        leadingImage = Icons.Default.Person,
        placeholder = stringResource(R.string.name),
        error = error,
        isError = isError,
        onValueChange = onValueChange
    )
}

@Composable
private fun CommonField(
    text: String,
    leadingImage: ImageVector,
    placeholder: String,
    error: String,
    isError: Boolean,
    onValueChange: (String) -> Unit,
) {
    Column {
        OutlinedTextField(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .sizeIn(minHeight = 56.dp)
                .fillMaxWidth(),
            value = text,
            leadingIcon = {
                Icon(
                    imageVector = leadingImage,
                    contentDescription = null,
                    tint = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled)
                )
            },
            isError = isError,
            onValueChange = { onValueChange(it) },
            placeholder = { Text(placeholder) },
            singleLine = true
        )
        ErrorText(
            text = error,
            visible = isError
        )
    }
}

@Composable
fun PasswordField(
    text: String,
    error: String,
    isError: Boolean,
    onValueChange: (String) -> Unit,
) {
    var showPassword by rememberSaveable { mutableStateOf(false) }
    Column {
        OutlinedTextField(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .sizeIn(minHeight = 56.dp)
                .fillMaxWidth(),
            value = text,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled)
                )
            },
            isError = isError,
            onValueChange = { onValueChange(it) },
            placeholder = { Text(stringResource(R.string.password)) },
            singleLine = true,
            visualTransformation = if (showPassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconToggleButton(
                    checked = showPassword,
                    onCheckedChange = { showPassword = it }
                ) {
                    Icon(
                        painterResource(
                            if (showPassword) R.drawable.ic_show_password else R.drawable.ic_hide_password
                        ),
                        tint = if (showPassword) {
                            MaterialTheme.colors.primary
                        } else {
                            MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled)
                        },
                        contentDescription = null
                    )
                }
            }
        )
        ErrorText(
            text = error,
            visible = isError
        )
    }
}

@Composable
private fun ErrorText(
    text: String = "Error message",
    visible: Boolean = false,
) {
    val alpha by animateFloatAsState(if (visible) 1f else 0f)
    Text(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .alpha(alpha),
        text = text,
        color = MaterialTheme.colors.error,
        style = MaterialTheme.typography.caption,
    )
}

@Composable
fun AuthButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Button(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .sizeIn(minHeight = 56.dp)
            .fillMaxWidth(),
        enabled = enabled,
        onClick = onClick
    ) {
        Text(text)
    }
}

@Composable
fun AuthProgressBar() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(MaterialTheme.colors.surface.copy(alpha = ContentAlpha.medium))
            .pointerInput(Unit) {}
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun AuthDropDownMenu(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    selectedItem: String,
    label: String,
    items: List<String>,
    onMenu: () -> Unit,
    onItem: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { onMenu() }
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selectedItem,
            singleLine = true,
            onValueChange = {},
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(
            modifier = Modifier.requiredHeightIn(max = 160.dp),
            expanded = expanded,
            onDismissRequest = onDismiss
        ) {
            items.forEach {
                DropdownMenuItem(onClick = { onItem(it) }) {
                    Text(it)
                }
            }
        }
    }
}
