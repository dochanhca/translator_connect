package com.example.translateconnector.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.translateconnector.R
import com.example.translateconnector.domain.viewmodel.LoginViewModel
import com.example.translateconnector.presentation.compose.CustomTextField
import com.example.translateconnector.presentation.theme.ui.dividerGrey
import com.example.translateconnector.presentation.theme.ui.salmonPink
import com.example.translateconnector.presentation.theme.ui.textBrown

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun LoginScreen() {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Login Page",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = textBrown
                        )
                    )
                },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            //Divider below the AppBar
            HorizontalDivider(
                color = dividerGrey,
                thickness = 1.dp,
            )
            LoginBody()
        }

    }
}

@Composable
fun LoginBody() {
    val viewModel = hiltViewModel<LoginViewModel>()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomTextField(
            value = viewModel.email.value,
            onValueChange = { viewModel.email.value = it },
            label = stringResource(id = R.string.MH06_005),
            isError = viewModel.isPhoneError.value,
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "E-Mail İcon",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )
        PhoneError(viewModel.isPhoneError.value)
        CustomTextField(
            value = viewModel.password.value,
            onValueChange = { viewModel.password.value = it },
            label = stringResource(id = R.string.MH05_003),
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Password İcon",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )

        Button(
            onClick = {
                /* TODO implement login */
            },
            modifier = Modifier.wrapContentWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(dimensionResource(id = R.dimen.common_corner_radius))
        ) {
            Text(
                text = stringResource(id = R.string.MH05_001),
                style = MaterialTheme.typography.labelLarge.copy(
                    color = Color.White
                ),
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.extra_margin_padding),
                    vertical = dimensionResource(id = R.dimen.common_margin_padding)
                )
            )

        }

    }

}

@Composable
fun PhoneError(isPhoneError: Boolean) {
    if (isPhoneError) { // Replace showPhoneNumberError with your logic
        Text(
            text = stringResource(id = R.string.TB_1003),
            style = MaterialTheme.typography.labelMedium.copy(
                color = salmonPink
            ),
            modifier = Modifier.padding(start = 35.dp)
        )
    }
}